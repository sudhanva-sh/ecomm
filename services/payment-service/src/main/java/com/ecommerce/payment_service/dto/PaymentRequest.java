package com.ecommerce.payment_service.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private Long orderId;
    private Double amount;
}
