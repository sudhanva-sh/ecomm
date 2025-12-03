package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrderByUser(Long userId);
    OrderResponse placeOrderAndPay(OrderRequest orderRequest);
    OrderResponse payExistingOrder(Long orderId);
    OrderResponse placeOrderAsync(OrderRequest request);
}
