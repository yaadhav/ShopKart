package com.shopkart.payment.service;

import com.shopkart.cart.internalagent.CartAgent;
import com.shopkart.catalog.internalagent.ProductAgent;
import com.shopkart.order.dto.enums.OrderStatus;
import com.shopkart.order.dto.enums.PaymentMode;
import com.shopkart.order.dto.enums.PaymentStatus;
import com.shopkart.payment.dto.enums.PaymentMethod;
import com.shopkart.payment.dto.request.RecordPaymentRequest;
import com.shopkart.payment.internalagent.OrderAgent;
import com.shopkart.payment.model.PaymentEntity;
import com.shopkart.payment.model.PaymentIntentEntity;
import com.shopkart.payment.repo.PaymentIntentRepo;
import com.shopkart.payment.repo.PaymentRepo;
import com.shopkart.payment.util.PaymentConstants;
import com.shopkart.payment.util.PaymentExceptionStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentIntentRepo paymentIntentRepo;
    private final ProductAgent productAgent;
    private final CartAgent cartAgent;
    private final OrderAgent orderAgent;
    private final PaymentRepo paymentRepo;

    @Transactional
    public Map<String, Object> recordPayment(RecordPaymentRequest request) {
        if(!PaymentConstants.Security.INBOUND_SECRET.equals(request.getSecretKey())) {
            throw PaymentExceptionStore.INVALID_SECRET_KEY.exception();
        }

        PaymentIntentEntity intent = paymentIntentRepo.findById(request.getPaymentIntentId())
                .orElseThrow(PaymentExceptionStore.PAYMENT_INTENT_NOT_FOUND::exception);

        if(intent.getPaymentStatus() != PaymentStatus.INITIATED.code) {
            throw PaymentExceptionStore.PAYMENT_ALREADY_PROCESSED.exception();
        }

        if(System.currentTimeMillis() - intent.getCreatedTime() > PaymentConstants.Security.PAYMENT_EXPIRY_MILLIS) {
            intent.setPaymentStatus(PaymentStatus.FAILED.code);
            paymentIntentRepo.save(intent);
            orderAgent.updateOrderPayment(intent.getOrderId(), null, PaymentStatus.FAILED.code, OrderStatus.PAYMENT_FAILED.code);
            throw PaymentExceptionStore.PAYMENT_EXPIRED.exception();
        }

        if(PaymentConstants.Keys.SUCCESS.equals(request.getStatus())) {
            if(intent.getTotalAmount().compareTo(request.getTotalAmount()) != 0) {
                throw PaymentExceptionStore.AMOUNT_MISMATCH.exception();
            }

            PaymentEntity payment = new PaymentEntity();
            payment.setOrderId(intent.getOrderId());
            payment.setUserId(intent.getUserId());
            payment.setPaymentIntentId(intent.getPaymentIntentId());
            payment.setPaymentMethod(PaymentMethod.getCode(request.getPaymentMethod()));
            payment.setPaymentMode(PaymentMode.ONLINE.code);
            payment.setTotalAmount(request.getTotalAmount());
            payment.setReferenceId(request.getReferenceId());
            paymentRepo.save(payment);

            intent.setPaymentStatus(PaymentStatus.PAID.code);
            paymentIntentRepo.save(intent);

            orderAgent.updateOrderPayment(intent.getOrderId(), payment.getPaymentId(), PaymentStatus.PAID.code, OrderStatus.CONFIRMED.code);

            cartAgent.clearCart(intent.getUserId());

            for(Map.Entry<Long, Map<Integer, Integer>> entry : orderAgent.getProductQuantityDetails(intent.getOrderId()).entrySet()) {
                Long productId = entry.getKey();
                for(Map.Entry<Integer, Integer> sizeEntry : entry.getValue().entrySet()) {
                    productAgent.deductStock(productId, sizeEntry.getKey(), sizeEntry.getValue());
                }
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put(PaymentConstants.Keys.STATUS, PaymentConstants.Keys.SUCCESS);
            response.put(PaymentConstants.Keys.PAYMENT_ID, payment.getPaymentId());
            response.put(PaymentConstants.Keys.ORDER_ID, intent.getOrderId());
            return response;
        } else {
            intent.setPaymentStatus(PaymentStatus.FAILED.code);
            paymentIntentRepo.save(intent);

            orderAgent.updateOrderPayment(intent.getOrderId(), null, PaymentStatus.FAILED.code, OrderStatus.PAYMENT_FAILED.code);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put(PaymentConstants.Keys.STATUS, PaymentConstants.Keys.FAILED);
            response.put(PaymentConstants.Keys.ORDER_ID, intent.getOrderId());
            return response;
        }
    }
}
