package com.shopkart.catalog.controller;

import com.shopkart.catalog.model.ProductDTO;
import com.shopkart.catalog.service.ProductFormatHandler;
import com.shopkart.catalog.service.ProductService;
import com.shopkart.catalog.util.CatalogConstants.Keys;
import com.shopkart.common.util.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductAPI {

    private final ProductService productService;
    private final ProductFormatHandler formatHandler;

    public ProductAPI(ProductService productService, ProductFormatHandler formatHandler) {
        this.productService = productService;
        this.formatHandler = formatHandler;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getProducts(@RequestParam Map<String, String> filters) {
        Page<ProductDTO> page = productService.getProducts(filters);
        List<Map<String, Object>> items = page.getContent().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(PagedResponse.from(page, items));
    }

    private Map<String, Object> toResponse(ProductDTO product) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(Keys.PRODUCT_ID, product.getProductId());
        map.put(Keys.NAME, product.getName());
        map.put(Keys.DESCRIPTION, product.getDescription());
        map.put(Keys.SELLING_PRICE, product.getSellingPrice());
        map.put(Keys.ORIGINAL_PRICE, product.getOriginalPrice());
        map.put(Keys.DISCOUNT_PERCENTAGE, product.getDiscountPercentage());
        map.put(Keys.RATING, product.getRating());
        map.put(Keys.BRAND, product.getBrand());
        map.put(Keys.FASHION_STYLE, product.getFashionStyle());
        map.put(Keys.CATEGORY, product.getCategory());
        map.put(Keys.OCCASION, product.getOccasion());
        map.put(Keys.SIZE, product.getSize());
        map.put(Keys.IMAGE, product.getImage());
        map.put(Keys.STOCK, product.getStock());
        map.putAll(formatHandler.addFormattedNodes(product));
        return map;
    }
}
