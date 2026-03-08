package com.shopkart.payment.service;

import com.shopkart.common.api.ExternalAPIClient;
import com.shopkart.payment.dto.response.ExternalAPIResponse;
import com.shopkart.payment.util.PaymentConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PaymentGatewayClient {

    private final ExternalAPIClient externalApiClient;

    public PaymentGatewayClient(ExternalAPIClient externalApiClient) {
        this.externalApiClient = externalApiClient;
    }

    public ExternalAPIResponse createPaymentMockSuccess(Long paymentIntentId, Long userId, BigDecimal totalAmount) {
        return ExternalAPIResponse.builder()
                .code(200)
                .status(PaymentConstants.Keys.SUCCESS)
                .message("Payment successful for intentId: " + paymentIntentId + ", userId: " + userId + ", amount: " + totalAmount)
                .isSuccess(true)
                .build();
    }

    public ExternalAPIResponse createPayment(Long paymentIntentId, Long userId, BigDecimal totalAmount) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(PaymentConstants.Keys.PAYMENT_INTENT_ID, paymentIntentId);
        payload.put(PaymentConstants.Keys.USER_ID, userId);
        payload.put(PaymentConstants.Keys.TOTAL_AMOUNT, totalAmount);
        payload.put(PaymentConstants.Keys.SECRET_KEY, PaymentConstants.Security.OUTBOUND_SECRET);

        ExternalAPIResponse externalAPIResponse = externalApiClient.invokePostAPI(PaymentConstants.API.CREATE_PAYMENT_URL, payload, ExternalAPIResponse.class);
        handleErrorPaymentResponse(externalAPIResponse);
        return externalAPIResponse;
    }

    private void handleErrorPaymentResponse(ExternalAPIResponse externalAPIResponse) {
        if(!(externalAPIResponse.getCode() >= 200 && externalAPIResponse.getCode() < 300)) {
            externalAPIResponse.setSuccess(false);
        }
    }
}
