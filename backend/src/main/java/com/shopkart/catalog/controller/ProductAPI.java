package com.shopkart.catalog.controller;

import com.shopkart.catalog.dto.request.RateProductRequest;
import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.service.ProductService;
import com.shopkart.catalog.util.ProductUtil;
import com.shopkart.common.util.PagedResponse;
import com.shopkart.user.util.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductAPI {

    private final ProductService productService;

    public ProductAPI(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(@RequestParam Map<String, String> filters) {
        Page<ProductResponse> page = productService.getProducts(filters);
        List<Map<String, Object>> items = page.getContent().stream()
                .map(ProductUtil::toResponse)
                .toList();
        return ResponseEntity.ok(PagedResponse.from(page, items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductDetails(@PathVariable Long id) {
        Map<String, Object> details = productService.getProductDetails(id);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/{productId}/rate")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> rateProduct(
            @PathVariable Long productId,
            @RequestBody RateProductRequest request) {
        Long userId = AuthUtil.getUserIdFromJwt();
        return ResponseEntity.ok(productService.rateProduct(userId, productId, request));
    }
}
