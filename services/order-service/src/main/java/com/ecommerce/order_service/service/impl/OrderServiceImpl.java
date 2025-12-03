package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.client.PaymentClient;
import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.entity.OrderStatus;
import com.ecommerce.order_service.events.OrderCreatedEvent;
import com.ecommerce.order_service.events.OrderItemEvent;
import com.ecommerce.order_service.kafka.OrderEventsProducer;
import com.ecommerce.order_service.repository.OrderItemRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;
    private final OrderEventsProducer orderEventsProducer;
    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        double totalAmount = 0;
        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .status(OrderStatus.CREATED)
                .build();

        List<OrderItem> items = orderRequest.getItems().stream()
                .map(item -> {

                    ProductResponse product = productClient.getProductById(item.getProductId());

                    if (product.getStock() < item.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName());
                    }

                    double price = product.getPrice() * item.getQuantity();
                    return OrderItem.builder()
                            .quantity(item.getQuantity())
                            .productId(item.getProductId())
                            .price(price)
                            .order(order)
                            .build();
                }).collect(Collectors.toList());

        for(OrderItem item : items){
            totalAmount += item.getPrice()* item.getQuantity();
        }
        order.setTotalAmount(totalAmount);
        order.setItems(items);

        orderRepository.save(order);
        return mapToResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found: "+orderId));
    }

    @Override
    public List<OrderResponse> getOrderByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse placeOrderAndPay(OrderRequest request) {
        double total = 0.0;
        Order order = Order.builder()
                .userId(request.getUserId())
                .status(OrderStatus.CREATED)
                .build();

        Order saved = orderRepository.save(order);
        // Create the Order
        List<OrderItem> itemList = request.getItems().stream().map(item ->{
            var product = productClient.getProductById(item.getProductId());
            if(product.getStock() < item.getQuantity()){
                throw  new RuntimeException("Insufficient stock for ProductId: " + product.getId());
            }

            return OrderItem.builder()
                    .productId(product.getId())
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .order(order)
                    .build();
        }).collect(Collectors.toList());

        total = itemList.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        order.setTotalAmount(total);
        order.setItems(itemList);

        // Order from created to pending.
        order.setStatus(OrderStatus.PENDING);
        saved = orderRepository.save(order);

        // Call payment service.

        PaymentRequest paymentRequest = new PaymentRequest(saved.getId(), saved.getTotalAmount());
        PaymentResponse paymentResponse;
        try{
            paymentResponse = paymentClient.makePayment(paymentRequest);
        }
        catch (Exception ex){

            throw new RuntimeException("Payment service unavailable: " + ex.getMessage());
        }

        if("Success".equalsIgnoreCase(paymentResponse.getStatus())){
            saved.setStatus(OrderStatus.CONFIRMED);
            // TODO: call product service to deduct stock
        }
        else {
            saved.setStatus(OrderStatus.CANCELLED);
        }

        saved=orderRepository.save(saved);
        return mapToResponse(order);
    }

    @Override
    public OrderResponse payExistingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();


        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), order.getTotalAmount());
        PaymentResponse paymentResponse;
        try{
            paymentResponse = paymentClient.makePayment(paymentRequest);
        }
        catch (Exception ex){

            throw new RuntimeException("Payment service unavailable: " + ex.getMessage());
        }

        if("Success".equalsIgnoreCase(paymentResponse.getStatus())){
            order.setStatus(OrderStatus.CONFIRMED);
            // TODO: call product service to deduct stock
        }
        else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        Order saved=orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    public OrderResponse placeOrderAsync(OrderRequest orderRequest){
        double totalAmount = 0;

        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .status(OrderStatus.CREATED)
                .build();
        Order saved = orderRepository.save(order);

        List<OrderItem> items = orderRequest.getItems().stream().map(item -> {
                    ProductResponse product = productClient.getProductById(item.getProductId());
                    return OrderItem.builder()
                            .productId(product.getId())
                            .quantity(item.getQuantity())
                            .price(product.getPrice())
                            .order(order)
                            .build();
        }).collect(Collectors.toList());

        totalAmount = items.stream().mapToDouble(i -> i.getQuantity() * i.getPrice()).sum();

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        order.setStatus(OrderStatus.PENDING);
        saved = orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .totalAmount(saved.getTotalAmount())
                .items(saved.getItems().stream().map(
                        i-> OrderItemEvent.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice()).build()).toList()).build();

        orderEventsProducer.publishOrderCreated(event);
        return mapToResponse(saved);
    }


    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .createdAt(order.getCreatedAt())
                .items(itemDTOs)
                .build();
    }
}
