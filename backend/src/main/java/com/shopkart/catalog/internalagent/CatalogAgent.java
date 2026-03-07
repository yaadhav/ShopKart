package com.shopkart.catalog.internalagent;

import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.model.ProductImageEntity;
import com.shopkart.catalog.repo.ProductImageRepo;
import com.shopkart.catalog.repo.ProductRepo;
import com.shopkart.catalog.util.CatalogExceptionStore;
import com.shopkart.catalog.util.ProductUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CatalogAgent {

    private final ProductRepo productRepo;
    private final ProductImageRepo productImageRepo;

    public CatalogAgent(ProductRepo productRepo, ProductImageRepo productImageRepo) {
        this.productRepo = productRepo;
        this.productImageRepo = productImageRepo;
    }

    public Map<String, Object> getProductInfo(Long productId) {
        var entity = productRepo.findById(productId)
                .orElseThrow(() -> CatalogExceptionStore.PRODUCT_NOT_FOUND.exception());
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
                .brand(com.shopkart.catalog.dto.enums.Brand.getName(entity.getBrand()))
                .fashionStyle(com.shopkart.catalog.dto.enums.FashionStyle.getName(entity.getFashionStyle()))
                .category(com.shopkart.catalog.dto.enums.Category.getName(entity.getCategory()))
                .occasion(com.shopkart.catalog.dto.enums.Occasion.getName(entity.getOccasion()))
                .image(thumbnail)
                .build();
        return ProductUtil.toResponse(response);
    }

    public List<Map<String, Object>> getProductInfoList(List<Long> productIds) {
        return productIds.stream()
                .map(this::getProductInfo)
                .toList();
    }
}
