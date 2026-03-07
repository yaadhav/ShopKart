package com.shopkart.catalog.internalagent;

import com.shopkart.catalog.dto.enums.*;
import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.model.ProductEntity;
import com.shopkart.catalog.model.ProductImageEntity;
import com.shopkart.catalog.model.ProductStockEntity;
import com.shopkart.catalog.repo.ProductImageRepo;
import com.shopkart.catalog.repo.ProductRepo;
import com.shopkart.catalog.repo.ProductStockRepo;
import com.shopkart.catalog.util.CatalogConstants;
import com.shopkart.catalog.util.CatalogExceptionStore;
import com.shopkart.catalog.util.ProductUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CatalogAgent {

    private final ProductRepo productRepo;
    private final ProductImageRepo productImageRepo;
    private final ProductStockRepo productStockRepo;

    public CatalogAgent(ProductRepo productRepo, ProductImageRepo productImageRepo, ProductStockRepo productStockRepo) {
        this.productRepo = productRepo;
        this.productImageRepo = productImageRepo;
        this.productStockRepo = productStockRepo;
    }

    public Map<String, Object> getProductDetails(Long productId, String size) {
        Map<String, Object> productDetails = getProductDetails(productId);
        Integer sizeCode = Size.getCode(size);
        Integer stockQuantity = getStockQuantity(productId, sizeCode);
        productDetails.put(CatalogConstants.Keys.SIZE, size);
        productDetails.put(CatalogConstants.Keys.QUANTITY, stockQuantity);
        return productDetails;
    }

    public Map<String, Object> getProductDetails(Long productId) {
        ProductEntity entity = productRepo.findById(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        String thumbnail = productImageRepo.findByProductIdAndIsThumbnail(productId, true)
                .map(ProductImageEntity::getImageUrl).orElse(null);

        ProductResponse response = ProductResponse.builder()
                .productId(entity.getProductId())
                .name(entity.getName())
                .tagline(entity.getTagline())
                .sellingPrice(entity.getSellingPrice())
                .originalPrice(entity.getOriginalPrice())
                .discountPercentage(entity.getDiscountPercentage())
                .rating(entity.getRating())
                .ratingCount(entity.getRatingCount())
                .brand(Brand.getName(entity.getBrand()))
                .fashionStyle(FashionStyle.getName(entity.getFashionStyle()))
                .category(Category.getName(entity.getCategory()))
                .occasion(Occasion.getName(entity.getOccasion()))
                .image(thumbnail)
                .build();

        return ProductUtil.toResponse(response);
    }

    public Integer getStockQuantity(Long productId, Integer sizeCode) {
        return productStockRepo.findByProductIdAndSize(productId, sizeCode)
                .map(ProductStockEntity::getQuantity)
                .orElse(0);
    }
}
