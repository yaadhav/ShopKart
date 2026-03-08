package com.shopkart.order.controller;

import com.shopkart.common.exception.GlobalExceptionHandler;
import com.shopkart.common.exception.ShopKartException;
import com.shopkart.order.dto.request.UpdateOrderStatusRequest;
import com.shopkart.order.service.OrderService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderAdminAPI.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class OrderAdminAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getOrderDetails_withExistingOrder_returns200() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("order_id", 1L);
        response.put("order_amount", new BigDecimal("999.00"));
        response.put("order_amount_formatted", "₹999.00");
        response.put("order_status", "confirmed");
        response.put("order_status_formatted", "Confirmed");
        response.put("order_total", new BigDecimal("1048.00"));
        response.put("order_total_formatted", "₹1,048.00");
        when(orderService.getOrderDetailsAdmin(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order_id").value(1))
                .andExpect(jsonPath("$.order_amount").value(999.00))
                .andExpect(jsonPath("$.order_status").value("confirmed"))
                .andExpect(jsonPath("$.order_status_formatted").value("Confirmed"));
    }

    @Test
    void getOrderDetails_withNonExistentOrder_returns404() throws Exception {
        when(orderService.getOrderDetailsAdmin(999L))
                .thenThrow(new ShopKartException(HttpStatus.NOT_FOUND, "ORDER_001", "Order not found"));

        mockMvc.perform(get("/api/v1/admin/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("ORDER_001"));
    }

    @Test
    void updateOrderStatus_withValidRequest_returns200() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("order_id", 1L);
        response.put("order_status", "shipped");
        response.put("order_status_formatted", "Shipped");
        when(orderService.updateOrderStatus(eq(1L), any(UpdateOrderStatusRequest.class))).thenReturn(response);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderStatus("shipped");

        mockMvc.perform(patch("/api/v1/admin/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order_id").value(1))
                .andExpect(jsonPath("$.order_status").value("shipped"))
                .andExpect(jsonPath("$.order_status_formatted").value("Shipped"));
    }
}
