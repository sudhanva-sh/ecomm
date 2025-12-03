package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.events.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsProducer {

    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    public static final String TOPIC = "order.created";

    public OrderEventsProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event){
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
    }
}
