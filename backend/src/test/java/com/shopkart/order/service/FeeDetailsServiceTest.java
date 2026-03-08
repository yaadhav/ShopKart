package com.shopkart.order.service;

import com.shopkart.order.dto.request.FeeDetailsRequest;
import com.shopkart.order.model.FeeDetailsEntity;
import com.shopkart.order.repo.FeeDetailsRepo;
import com.shopkart.order.util.OrderConstants.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeDetailsServiceTest {

    @Mock
    private FeeDetailsRepo feeDetailsRepo;

    @InjectMocks
    private FeeDetailsService feeDetailsService;

    @Test
    void getFeeDetails_withExistingRecord_returnsFormattedResponse() {
        FeeDetailsEntity entity = FeeDetailsEntity.builder()
                .feeDetailsId(1L)
                .deliveryFee(new BigDecimal("49.00"))
                .platformFee(new BigDecimal("10.00"))
                .updatedBy(100L)
                .build();
        when(feeDetailsRepo.findTopByOrderByFeeDetailsIdDesc()).thenReturn(Optional.of(entity));

        Map<String, Object> result = feeDetailsService.getFeeDetails();

        assertEquals(1L, result.get(Keys.FEE_DETAILS_ID));
        assertEquals(new BigDecimal("49.00"), result.get(Keys.DELIVERY_FEE));
        assertNotNull(result.get(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX));
        assertEquals(new BigDecimal("10.00"), result.get(Keys.PLATFORM_FEE));
        assertNotNull(result.get(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX));
        assertEquals(100L, result.get(Keys.UPDATED_BY));
    }

    @Test
    void getFeeDetails_withNoRecord_returnsZeroDefaults() {
        when(feeDetailsRepo.findTopByOrderByFeeDetailsIdDesc()).thenReturn(Optional.empty());

        Map<String, Object> result = feeDetailsService.getFeeDetails();

        assertNull(result.get(Keys.FEE_DETAILS_ID));
        assertEquals(BigDecimal.ZERO, result.get(Keys.DELIVERY_FEE));
        assertEquals(BigDecimal.ZERO, result.get(Keys.PLATFORM_FEE));
        assertNull(result.get(Keys.UPDATED_BY));
    }

    @Test
    void createOrUpdateFeeDetails_createsNewEntityAndReturnsResponse() {
        FeeDetailsRequest request = new FeeDetailsRequest(new BigDecimal("59.00"), new BigDecimal("15.00"));

        FeeDetailsEntity savedEntity = FeeDetailsEntity.builder()
                .feeDetailsId(2L)
                .deliveryFee(new BigDecimal("59.00"))
                .platformFee(new BigDecimal("15.00"))
                .updatedBy(200L)
                .build();
        when(feeDetailsRepo.save(any(FeeDetailsEntity.class))).thenReturn(savedEntity);

        Map<String, Object> result = feeDetailsService.createOrUpdateFeeDetails(200L, request);

        assertEquals(2L, result.get(Keys.FEE_DETAILS_ID));
        assertEquals(new BigDecimal("59.00"), result.get(Keys.DELIVERY_FEE));
        assertNotNull(result.get(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX));
        assertEquals(new BigDecimal("15.00"), result.get(Keys.PLATFORM_FEE));
        assertNotNull(result.get(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX));
        assertEquals(200L, result.get(Keys.UPDATED_BY));
        verify(feeDetailsRepo).save(any(FeeDetailsEntity.class));
    }

    @Test
    void createOrUpdateFeeDetails_insertsNewRowPreservingHistory() {
        FeeDetailsRequest request = new FeeDetailsRequest(new BigDecimal("30.00"), new BigDecimal("5.00"));

        FeeDetailsEntity savedEntity = FeeDetailsEntity.builder()
                .feeDetailsId(3L)
                .deliveryFee(new BigDecimal("30.00"))
                .platformFee(new BigDecimal("5.00"))
                .updatedBy(300L)
                .build();
        when(feeDetailsRepo.save(any(FeeDetailsEntity.class))).thenAnswer(invocation -> {
            FeeDetailsEntity arg = invocation.getArgument(0);
            assertNull(arg.getFeeDetailsId());
            assertEquals(300L, arg.getUpdatedBy());
            return savedEntity;
        });

        Map<String, Object> result = feeDetailsService.createOrUpdateFeeDetails(300L, request);

        assertEquals(3L, result.get(Keys.FEE_DETAILS_ID));
    }
}
