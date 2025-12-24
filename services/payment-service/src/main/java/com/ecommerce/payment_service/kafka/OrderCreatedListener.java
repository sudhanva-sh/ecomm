package com.ecommerce.payment_service.kafka;

import com.ecommerce.payment_service.entity.Payment;
import com.ecommerce.payment_service.entity.PaymentStatus;
import com.ecommerce.payment_service.events.OrderCreatedEvent;
import com.ecommerce.payment_service.events.PaymentEvent;
import com.ecommerce.payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderCreatedListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentRepository paymentRepository;
    private final String TOPIC = "payment.completed";

    public OrderCreatedListener(PaymentRepository paymentRepository,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order.created",
            groupId = "payment-service-group"
//            containerFactory = "kafkaListenerContainerFactory"
            )
    public void handleOrderEvent(OrderCreatedEvent event) {


        logger.info(String.format("Received from Order Service: %s", event.toString()));
        Payment existing = paymentRepository.findByOrderId(event.getOrderId());
        if (existing != null) {
            // already processed — emit event if necessary or skip

            logger.info(String.format("Event already present %s", existing.toString()));

            PaymentEvent alreadyEvent = PaymentEvent.builder()
                    .orderId(existing.getOrderId())
                    .status(existing.getStatus().name())
                    .transactionId(existing.getTransactionId())
                    .totalAmount(existing.getAmount())
                    .processedTime(existing.getCreatedAt())
                    .build();
            kafkaTemplate.send(TOPIC, alreadyEvent.getOrderId().toString(), alreadyEvent);
            return;
        }

        logger.info("Processing Payment");
        // Simulate payment processing
        boolean success = Math.random() > 0.2; // 80% success rate
        PaymentStatus status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getTotalAmount())
                .status(status)
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

        PaymentEvent completed = PaymentEvent.builder()
                .orderId(payment.getOrderId())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .totalAmount(payment.getAmount())
                .processedTime(payment.getCreatedAt())
                .build();

        logger.info("Payment process done");
        logger.info(String.format("Sending response payment.completed %s", completed.toString()));
        kafkaTemplate.send(TOPIC, completed.getOrderId().toString(), completed);
    }
}
