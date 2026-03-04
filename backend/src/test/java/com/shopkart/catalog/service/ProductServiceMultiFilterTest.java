package com.shopkart.catalog.service;

import com.shopkart.catalog.dto.ProductEntity;
import com.shopkart.catalog.model.ProductDTO;
import com.shopkart.catalog.repo.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceMultiFilterTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testMultiSelectFilter() {
        Map<String, String> params = new HashMap<>();
        params.put("brand.zenfit,urbanedge", "");
        params.put("page", "0");
        params.put("page_size", "20");
        
        Page<ProductDTO> result = productService.getProducts(params);
        assertNotNull(result);
        
        // All results should have brand zenfit or urbanedge
        result.getContent().forEach(product -> {
            String brand = product.getBrand().toLowerCase();
            assertTrue(brand.equals("zenfit") || brand.equals("urbanedge"),
                "Product brand should be zenfit or urbanedge, but was: " + brand);
        });
    }

    @Test
    public void testSingleFilter() {
        Map<String, String> params = new HashMap<>();
        params.put("brand.zenfit", "");
        params.put("page", "0");
        params.put("page_size", "20");
        
        Page<ProductDTO> result = productService.getProducts(params);
        assertNotNull(result);
        
        // All results should have brand zenfit
        result.getContent().forEach(product -> {
            assertEquals("zenfit", product.getBrand().toLowerCase(),
                "Product brand should be zenfit");
        });
    }

    @Test
    public void testMultipleFieldFilters() {
        Map<String, String> params = new HashMap<>();
        params.put("brand.zenfit,urbanedge", "");
        params.put("fashion_style.men", "");
        params.put("page", "0");
        params.put("page_size", "20");
        
        Page<ProductDTO> result = productService.getProducts(params);
        assertNotNull(result);
        
        // All results should match both filters
        result.getContent().forEach(product -> {
            String brand = product.getBrand().toLowerCase();
            assertTrue(brand.equals("zenfit") || brand.equals("urbanedge"));
            assertEquals("men", product.getFashionStyle().toLowerCase());
        });
    }
}
