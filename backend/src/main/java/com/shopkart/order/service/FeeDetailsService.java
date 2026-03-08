package com.shopkart.order.service;

import com.shopkart.common.util.CurrencyUtil;
import com.shopkart.order.dto.request.FeeDetailsRequest;
import com.shopkart.order.model.FeeDetailsEntity;
import com.shopkart.order.repo.FeeDetailsRepo;
import com.shopkart.order.util.OrderConstants.Keys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FeeDetailsService {

    private final FeeDetailsRepo feeDetailsRepo;

    public FeeDetailsService(FeeDetailsRepo feeDetailsRepo) {
        this.feeDetailsRepo = feeDetailsRepo;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFeeDetails() {
        FeeDetailsEntity entity = feeDetailsRepo.findTopByOrderByFeeDetailsIdDesc().orElse(null);

        if(entity == null) {
            return buildResponse(null, BigDecimal.ZERO, BigDecimal.ZERO, null);
        }

        return buildResponse(entity.getFeeDetailsId(), entity.getDeliveryFee(), entity.getPlatformFee(), entity.getUpdatedBy());
    }

    @Transactional
    public Map<String, Object> createOrUpdateFeeDetails(Long userId, FeeDetailsRequest request) {
        FeeDetailsEntity entity = FeeDetailsEntity.builder()
                .deliveryFee(request.getDeliveryFee())
                .platformFee(request.getPlatformFee())
                .updatedBy(userId)
                .build();

        entity = feeDetailsRepo.save(entity);

        return buildResponse(entity.getFeeDetailsId(), entity.getDeliveryFee(), entity.getPlatformFee(), entity.getUpdatedBy());
    }

    private Map<String, Object> buildResponse(Long feeDetailsId, BigDecimal deliveryFee, BigDecimal platformFee, Long updatedBy) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.FEE_DETAILS_ID, feeDetailsId);
        response.put(Keys.DELIVERY_FEE, deliveryFee);
        response.put(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(deliveryFee));
        response.put(Keys.PLATFORM_FEE, platformFee);
        response.put(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(platformFee));
        response.put(Keys.UPDATED_BY, updatedBy);
        return response;
    }
}
