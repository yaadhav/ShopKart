package com.shopkart.order.controller;

import com.shopkart.order.dto.request.CancelPaymentRequest;
import com.shopkart.order.dto.request.CreateOrderRequest;
import com.shopkart.order.service.OrderService;
import com.shopkart.user.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderAPI {

    private final OrderService orderService;

    public OrderAPI(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = AuthUtil.getUserIdFromJwt();
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> getOrders(@RequestParam Map<String, String> params) {
        Long userId = AuthUtil.getUserIdFromJwt();
        return ResponseEntity.ok(orderService.getOrders(userId, params));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Long orderId) {
        Long userId = AuthUtil.getUserIdFromJwt();
        return ResponseEntity.ok(orderService.getOrderDetails(userId, orderId));
    }

    @PostMapping("/cancel-payment")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> cancelPayment(@RequestBody CancelPaymentRequest request) {
        Long userId = AuthUtil.getUserIdFromJwt();
        return ResponseEntity.ok(orderService.cancelPayment(userId, request));
    }
}
