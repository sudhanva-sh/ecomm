package com.ecommerce.order_service.controller;


import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
        return ResponseEntity.ok(orderService.placeOrder(orderRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrderByUser(userId));
    }

    @PostMapping("/place-and-pay")
    public ResponseEntity<OrderResponse> placeAndPay(@RequestBody OrderRequest request) {
//        return ResponseEntity.ok(orderService.placeOrderAndPay(request));
        return ResponseEntity.ok(orderService.placeOrderAsync(request));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long orderId){
        return  ResponseEntity.ok(orderService.payExistingOrder(orderId));
    }
}
