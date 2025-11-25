package com.ecommerce.payment_service.service.impl;

import com.ecommerce.payment_service.dto.PaymentRequest;
import com.ecommerce.payment_service.dto.PaymentResponse;
import com.ecommerce.payment_service.entity.Payment;
import com.ecommerce.payment_service.entity.PaymentStatus;
import com.ecommerce.payment_service.repository.PaymentRepository;
import com.ecommerce.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {
        PaymentStatus status = new Random().nextBoolean() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .status(status)
                .amount(paymentRequest.getAmount())
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);

        if(payment == null){
            throw new RuntimeException("Payment with orderId not found" + orderId);
        }
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment){
        return  PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().toString())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
