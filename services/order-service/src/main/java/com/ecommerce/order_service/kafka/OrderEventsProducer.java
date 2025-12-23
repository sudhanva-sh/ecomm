package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.events.OrderCreatedEvent;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
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

        Message<OrderCreatedEvent> orderEventCreated = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("__TypeId__", "orderEventCreated")
                .build();

        logger.info(String.format("Sending :%s", orderEventCreated.toString()));
        kafkaTemplate.send(orderEventCreated);
    }
}
