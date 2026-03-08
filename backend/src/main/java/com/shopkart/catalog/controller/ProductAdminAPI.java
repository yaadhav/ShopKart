package com.shopkart.catalog.controller;

import com.shopkart.catalog.dto.request.AddStockRequest;
import com.shopkart.catalog.dto.request.CreateProductRequest;
import com.shopkart.catalog.dto.request.UpdateProductRequest;
import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.service.ProductService;
import com.shopkart.catalog.util.ProductUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasAnyRole('owner', 'super_admin', 'admin', 'product_admin')")
public class ProductAdminAPI {

    private final ProductService productService;

    public ProductAdminAPI(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestBody @Valid CreateProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductUtil.toResponse(product));
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<Map<String, Object>> getOutOfStockProducts() {
        return ResponseEntity.ok(productService.getOutOfStockProducts());
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @GetMapping("/{productId}/stock")
    public ResponseEntity<Map<String, Object>> getProductStock(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductStock(productId));
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<Map<String, Object>> addStock(
            @PathVariable Long productId,
            @RequestBody AddStockRequest request) {
        return ResponseEntity.ok(productService.addStock(productId, request));
    }
}
