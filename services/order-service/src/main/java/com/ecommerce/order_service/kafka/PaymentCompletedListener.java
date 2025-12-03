package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderStatus;
import com.ecommerce.order_service.events.PaymentEvent;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedListener {

    private final OrderRepository orderRepository;
    public PaymentCompletedListener(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment.completed",
                    groupId = "order-service-group")
    public void onPaymentCompleted(PaymentEvent event){
        Order order = orderRepository.findById(event.getOrderId()).get();

        if("success".equalsIgnoreCase(event.getStatus())){
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        orderRepository.save(order);
    }
}
