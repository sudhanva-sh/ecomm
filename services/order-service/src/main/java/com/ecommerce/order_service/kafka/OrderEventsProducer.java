package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventsProducer.class);
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    public static final String TOPIC = "order.created";

    public OrderEventsProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event){

        logger.info(String.format("Sending :%s", event.toString()));
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
    }
}
