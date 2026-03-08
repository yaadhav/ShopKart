package com.shopkart.order.controller;

import tools.jackson.databind.ObjectMapper;
import com.shopkart.common.exception.GlobalExceptionHandler;
import com.shopkart.order.dto.request.FeeDetailsRequest;
import com.shopkart.order.service.FeeDetailsService;
import com.shopkart.order.util.OrderConstants.Keys;
import com.shopkart.user.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminFeeAPI.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AdminFeeAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FeeDetailsService feeDetailsService;

    @Test
    void getFeeDetails_returns200WithFeeData() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.FEE_DETAILS_ID, 1L);
        response.put(Keys.DELIVERY_FEE, new BigDecimal("49.00"));
        response.put(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX, "₹49.00");
        response.put(Keys.PLATFORM_FEE, new BigDecimal("10.00"));
        response.put(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX, "₹10.00");
        response.put(Keys.UPDATED_BY, 100L);
        when(feeDetailsService.getFeeDetails()).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/fees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee_details_id").value(1))
                .andExpect(jsonPath("$.delivery_fee").value(49.00))
                .andExpect(jsonPath("$.delivery_fee_formatted").value("₹49.00"))
                .andExpect(jsonPath("$.platform_fee").value(10.00))
                .andExpect(jsonPath("$.platform_fee_formatted").value("₹10.00"))
                .andExpect(jsonPath("$.updated_by").value(100));
    }

    @Test
    void getFeeDetails_withNoRecord_returns200WithDefaults() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.FEE_DETAILS_ID, null);
        response.put(Keys.DELIVERY_FEE, BigDecimal.ZERO);
        response.put(Keys.PLATFORM_FEE, BigDecimal.ZERO);
        response.put(Keys.UPDATED_BY, null);
        when(feeDetailsService.getFeeDetails()).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/fees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee_details_id").isEmpty())
                .andExpect(jsonPath("$.delivery_fee").value(0))
                .andExpect(jsonPath("$.platform_fee").value(0));
    }

    @Test
    void createOrUpdateFeeDetails_returns201WithCreatedData() throws Exception {
        try (MockedStatic<AuthUtil> authUtilMock = mockStatic(AuthUtil.class)) {
            authUtilMock.when(AuthUtil::getUserIdFromJwt).thenReturn(200L);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put(Keys.FEE_DETAILS_ID, 2L);
            response.put(Keys.DELIVERY_FEE, new BigDecimal("59.00"));
            response.put(Keys.DELIVERY_FEE + Keys.FORMATTED_SUFFIX, "₹59.00");
            response.put(Keys.PLATFORM_FEE, new BigDecimal("15.00"));
            response.put(Keys.PLATFORM_FEE + Keys.FORMATTED_SUFFIX, "₹15.00");
            response.put(Keys.UPDATED_BY, 200L);
            when(feeDetailsService.createOrUpdateFeeDetails(eq(200L), any(FeeDetailsRequest.class))).thenReturn(response);

            FeeDetailsRequest request = new FeeDetailsRequest(new BigDecimal("59.00"), new BigDecimal("15.00"));

            mockMvc.perform(post("/api/v1/admin/fees")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.fee_details_id").value(2))
                    .andExpect(jsonPath("$.delivery_fee").value(59.00))
                    .andExpect(jsonPath("$.platform_fee").value(15.00))
                    .andExpect(jsonPath("$.updated_by").value(200));
        }
    }
}
