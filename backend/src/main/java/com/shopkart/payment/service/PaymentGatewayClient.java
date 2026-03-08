package com.shopkart.payment.service;

import com.shopkart.common.api.ExternalApiClient;
import com.shopkart.payment.dto.response.PaymentResponse;
import com.shopkart.payment.util.PaymentConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PaymentGatewayClient {

    private final ExternalApiClient externalApiClient;

    public PaymentGatewayClient(ExternalApiClient externalApiClient) {
        this.externalApiClient = externalApiClient;
    }

    public PaymentResponse createPaymentMockSuccess(Long paymentIntentId, Long userId, BigDecimal totalAmount) {
        return PaymentResponse.builder()
                .code(200)
                .status(PaymentConstants.Keys.SUCCESS)
                .message("Payment successful for intentId: " + paymentIntentId + ", userId: " + userId + ", amount: " + totalAmount)
                .isSuccess(true)
                .build();
    }

    public PaymentResponse createPayment(Long paymentIntentId, Long userId, BigDecimal totalAmount) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(PaymentConstants.Keys.PAYMENT_INTENT_ID, paymentIntentId);
        payload.put(PaymentConstants.Keys.USER_ID, userId);
        payload.put(PaymentConstants.Keys.TOTAL_AMOUNT, totalAmount);
        payload.put(PaymentConstants.Keys.SECRET_KEY, PaymentConstants.Security.OUTBOUND_SECRET);

        PaymentResponse paymentResponse = externalApiClient.invokePostAPI(PaymentConstants.API.CREATE_PAYMENT_URL, payload, PaymentResponse.class);
        handleErrorPaymentResponse(paymentResponse);
        return paymentResponse;
    }

    private void handleErrorPaymentResponse(PaymentResponse paymentResponse) {
        if(!(paymentResponse.getCode()>=200 && paymentResponse.getCode()<300)) {
            paymentResponse.setSuccess(false);
        }
    }
}
