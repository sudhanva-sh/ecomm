package com.ecommerce.payment_service.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private Double amount;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
}
