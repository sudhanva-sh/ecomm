package com.ecommerce.payment_service.service;

import com.ecommerce.payment_service.dto.PaymentRequest;
import com.ecommerce.payment_service.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse makePayment(PaymentRequest paymentRequest);
    PaymentResponse getPaymentByOrderId(Long orderId);
}
