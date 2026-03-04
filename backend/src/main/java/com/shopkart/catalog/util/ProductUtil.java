package com.shopkart.catalog.util;

import com.shopkart.catalog.model.ProductDTO;
import com.shopkart.catalog.service.ProductFormatHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductUtil {
    public static Map<String, Object> toResponse(ProductDTO product) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(CatalogConstants.Keys.PRODUCT_ID, product.getProductId());
        map.put(CatalogConstants.Keys.NAME, product.getName());
        map.put(CatalogConstants.Keys.DESCRIPTION, product.getDescription());
        map.put(CatalogConstants.Keys.SELLING_PRICE, product.getSellingPrice());
        map.put(CatalogConstants.Keys.ORIGINAL_PRICE, product.getOriginalPrice());
        map.put(CatalogConstants.Keys.DISCOUNT_PERCENTAGE, product.getDiscountPercentage());
        map.put(CatalogConstants.Keys.RATING, product.getRating());
        map.put(CatalogConstants.Keys.RATING_COUNT, product.getRatingCount());
        map.put(CatalogConstants.Keys.BRAND, product.getBrand());
        map.put(CatalogConstants.Keys.FASHION_STYLE, product.getFashionStyle());
        map.put(CatalogConstants.Keys.CATEGORY, product.getCategory());
        map.put(CatalogConstants.Keys.OCCASION, product.getOccasion());
        map.put(CatalogConstants.Keys.SIZE, product.getSize());
        map.put(CatalogConstants.Keys.IMAGE, product.getImage());
        map.putAll(ProductFormatHandler.addFormattedNodes(product));
        return map;
    }
}
