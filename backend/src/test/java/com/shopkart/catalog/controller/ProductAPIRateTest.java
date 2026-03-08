package com.shopkart.catalog.controller;

import com.shopkart.catalog.dto.request.RateProductRequest;
import com.shopkart.catalog.service.ProductService;
import com.shopkart.common.exception.GlobalExceptionHandler;
import com.shopkart.common.exception.ShopKartException;
import com.shopkart.user.util.AuthUtil;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductAPI.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ProductAPIRateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private MockedStatic<AuthUtil> authUtilMock;

    @BeforeEach
    void setUp() {
        authUtilMock = Mockito.mockStatic(AuthUtil.class);
        authUtilMock.when(AuthUtil::getUserIdFromJwt).thenReturn(100L);
    }

    @AfterEach
    void tearDown() {
        authUtilMock.close();
    }

    @Test
    void rateProduct_withValidRequest_returns200() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("product_id", 1L);
        response.put("rating", 4);
        response.put("average_rating", new BigDecimal("4.0"));
        response.put("rating_count", 1);
        when(productService.rateProduct(eq(100L), eq(1L), any(RateProductRequest.class)))
                .thenReturn(response);

        RateProductRequest request = new RateProductRequest();
        request.setRating(4);

        mockMvc.perform(post("/api/v1/products/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(1))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.average_rating").value(4.0))
                .andExpect(jsonPath("$.rating_count").value(1));
    }

    @Test
    void rateProduct_withInvalidRating_returns400() throws Exception {
        when(productService.rateProduct(eq(100L), eq(1L), any(RateProductRequest.class)))
                .thenThrow(new ShopKartException(HttpStatus.BAD_REQUEST, "CATALOG_008", "Rating must be between 1 and 5"));

        RateProductRequest request = new RateProductRequest();
        request.setRating(6);

        mockMvc.perform(post("/api/v1/products/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("CATALOG_008"));
    }

    @Test
    void rateProduct_withNonExistentProduct_returns404() throws Exception {
        when(productService.rateProduct(eq(100L), eq(999L), any(RateProductRequest.class)))
                .thenThrow(new ShopKartException(HttpStatus.NOT_FOUND, "CATALOG_001", "Product not found"));

        RateProductRequest request = new RateProductRequest();
        request.setRating(3);

        mockMvc.perform(post("/api/v1/products/999/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("CATALOG_001"));
    }

    @Test
    void rateProduct_withUpdatedRating_returnsNewAverage() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("product_id", 1L);
        response.put("rating", 5);
        response.put("average_rating", new BigDecimal("4.5"));
        response.put("rating_count", 2);
        when(productService.rateProduct(eq(100L), eq(1L), any(RateProductRequest.class)))
                .thenReturn(response);

        RateProductRequest request = new RateProductRequest();
        request.setRating(5);

        mockMvc.perform(post("/api/v1/products/1/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.average_rating").value(4.5))
                .andExpect(jsonPath("$.rating_count").value(2));
    }
}
