package com.shopkart.payment.controller;

import com.shopkart.payment.dto.request.RecordPaymentRequest;
import com.shopkart.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentAPI {

    private final PaymentService paymentService;

    public PaymentAPI(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordPayment(@RequestBody RecordPaymentRequest request) {
        return ResponseEntity.ok(paymentService.recordPayment(request));
    }
}
