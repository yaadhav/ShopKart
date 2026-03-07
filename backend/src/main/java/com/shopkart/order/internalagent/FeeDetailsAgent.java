package com.shopkart.order.internalagent;

import com.shopkart.order.internalagent.resource.FeeResource;
import com.shopkart.order.model.FeeDetailsEntity;
import com.shopkart.order.repo.FeeDetailsRepo;
import org.springframework.stereotype.Component;

@Component
public class FeeDetailsAgent {

    private final FeeDetailsRepo feeDetailsRepo;

    public FeeDetailsAgent(FeeDetailsRepo feeDetailsRepo) {
        this.feeDetailsRepo = feeDetailsRepo;
    }

    public FeeResource getConvenienceFees() {
        FeeDetailsEntity entity = feeDetailsRepo.findTopByOrderByFeeDetailsIdDesc().orElse(new FeeDetailsEntity());

        return FeeResource.builder()
                .feeDetailsId(entity.getFeeDetailsId())
                .deliveryFee(entity.getDeliveryFee())
                .platformFee(entity.getPlatformFee())
                .build();
    }
}
