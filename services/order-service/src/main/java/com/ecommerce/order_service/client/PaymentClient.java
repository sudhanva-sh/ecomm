package com.ecommerce.order_service.client;


import com.ecommerce.order_service.dto.PaymentRequest;
import com.ecommerce.order_service.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service", url = "http://localhost:8084/")
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse makePayment(PaymentRequest request);
}
