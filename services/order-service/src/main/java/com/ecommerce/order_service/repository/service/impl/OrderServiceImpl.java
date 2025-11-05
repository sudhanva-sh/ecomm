package com.ecommerce.order_service.repository.service.impl;

import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.OrderItemResponse;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.dto.ProductResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.entity.OrderStatus;
import com.ecommerce.order_service.repository.OrderItemRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;

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
