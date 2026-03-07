package com.shopkart.order.internalagent;

import com.shopkart.order.model.FeeDetailsEntity;
import com.shopkart.order.repo.FeeDetailsRepo;
import org.springframework.stereotype.Component;

@Component
public class OrderAgent {

    private final FeeDetailsRepo feeDetailsRepo;

    public OrderAgent(FeeDetailsRepo feeDetailsRepo) {
        this.feeDetailsRepo = feeDetailsRepo;
    }

    public FeeDetailsEntity getCurrentFees() {
        return feeDetailsRepo.findTopByOrderByFeeDetailsIdDesc().orElse(null);
    }
}
