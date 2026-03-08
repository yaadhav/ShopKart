package com.shopkart.catalog.controller;

import com.shopkart.catalog.dto.request.CreateProductRequest;
import com.shopkart.catalog.dto.request.UpdateProductRequest;
import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.service.ProductService;
import com.shopkart.common.exception.GlobalExceptionHandler;
import com.shopkart.common.exception.ShopKartException;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductAdminAPI.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ProductAdminAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void updateProduct_withValidRequest_returns200() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("product_id", 1L);
        response.put("name", "Updated Tee");
        response.put("selling_price", new BigDecimal("799.00"));
        response.put("selling_price_formatted", "₹799.00");
        response.put("brand", "zenfit");
        response.put("brand_formatted", "Zenfit");
        when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class))).thenReturn(response);

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Tee");
        request.setSellingPrice(new BigDecimal("799.00"));

        mockMvc.perform(patch("/api/v1/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Tee"))
                .andExpect(jsonPath("$.selling_price").value(799.00))
                .andExpect(jsonPath("$.brand").value("zenfit"));
    }

    @Test
    void updateProduct_withNonExistentProduct_returns404() throws Exception {
        when(productService.updateProduct(eq(999L), any(UpdateProductRequest.class)))
                .thenThrow(new ShopKartException(HttpStatus.NOT_FOUND, "CATALOG_001", "Product not found"));

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Ghost Product");

        mockMvc.perform(patch("/api/v1/admin/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("CATALOG_001"));
    }

    @Test
    void createProduct_withValidRequest_returns201() throws Exception {
        ProductResponse productResponse = ProductResponse.builder()
                .productId(1L)
                .name("Classic Tee")
                .sellingPrice(new BigDecimal("999.00"))
                .originalPrice(new BigDecimal("1299.00"))
                .discountPercentage(23)
                .rating(BigDecimal.ZERO)
                .ratingCount(0)
                .brand("zenfit")
                .fashionStyle("men")
                .category("tshirts")
                .occasion("casual")
                .build();
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(productResponse);

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Classic Tee");
        request.setTagline("Everyday essential");
        request.setSellingPrice(new BigDecimal("999.00"));
        request.setOriginalPrice(new BigDecimal("1299.00"));
        request.setDiscountPercentage(23);
        request.setBrand("zenfit");
        request.setFashionStyle("men");
        request.setCategory("tshirts");
        request.setOccasion("casual");
        request.setStock(Map.of("S", 10, "M", 20, "L", 15));
        request.setImages(List.of("https://cdn.shopkart.com/img/classic-tee.jpg"));

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product_id").value(1))
                .andExpect(jsonPath("$.name").value("Classic Tee"));
    }
}
