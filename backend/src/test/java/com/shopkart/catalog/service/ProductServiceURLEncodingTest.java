package com.shopkart.catalog.service;

import com.shopkart.catalog.dto.response.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceURLEncodingTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testCommaInParameterKey() {
        // This simulates what Spring Boot receives after URL decoding
        // Browser sends: brand.zenfit%2Curbanedge
        // Spring Boot receives: brand.zenfit,urbanedge
        Map<String, String> params = new HashMap<>();
        params.put("brand.zenfit,urbanedge", "");
        params.put("page", "0");
        params.put("page_size", "20");
        
        Page<ProductResponse> result = productService.getProducts(params);
        assertNotNull(result);
        
        // Verify we get products with either brand
        result.getContent().forEach(product -> {
            String brand = product.getBrand().toLowerCase();
            assertTrue(brand.equals("zenfit") || brand.equals("urbanedge"),
                "Expected zenfit or urbanedge but got: " + brand);
        });
        
        System.out.println("✅ URL encoding test passed - comma in parameter key works correctly");
    }
}
