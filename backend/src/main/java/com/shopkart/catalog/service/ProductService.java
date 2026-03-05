package com.shopkart.catalog.service;

import com.shopkart.catalog.dto.CreateProductRequest;
import com.shopkart.catalog.dto.ProductEntity;
import com.shopkart.catalog.model.ProductDTO;
import com.shopkart.catalog.model.enums.Brand;
import com.shopkart.catalog.model.enums.Category;
import com.shopkart.catalog.model.enums.FashionStyle;
import com.shopkart.catalog.model.enums.Occasion;
import com.shopkart.catalog.model.enums.Size;
import com.shopkart.catalog.repo.ProductRepo;
import com.shopkart.catalog.util.CatalogConstants;
import com.shopkart.catalog.util.CatalogConstants.Entity;
import com.shopkart.catalog.util.CatalogConstants.Keys;
import com.shopkart.catalog.util.CatalogExceptionStore;
import com.shopkart.common.util.PageRequestBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepo productRepo;
    private final com.shopkart.catalog.repository.ProductDetailsRepo productDetailsRepo;
    private final com.shopkart.catalog.repository.ProductImageRepo productImageRepo;
    private final com.shopkart.catalog.repository.ProductStockRepo productStockRepo;

    public ProductService(ProductRepo productRepo, 
            com.shopkart.catalog.repository.ProductDetailsRepo productDetailsRepo,
            com.shopkart.catalog.repository.ProductImageRepo productImageRepo,
            com.shopkart.catalog.repository.ProductStockRepo productStockRepo) {
        this.productRepo = productRepo;
        this.productDetailsRepo = productDetailsRepo;
        this.productImageRepo = productImageRepo;
        this.productStockRepo = productStockRepo;
    }

    public Page<ProductDTO> getProducts(Map<String, String> paramMap) {
        Pageable pageable = PageRequestBuilder.build(paramMap, CatalogConstants.DEFAULT_SORT_KEY, CatalogConstants.SORT_FIELD_MAP);

        Specification<ProductEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            try {
                addFilterPredicate(paramMap, Keys.BRAND, Entity.BRAND, Brand.class, root, cb, predicates);
                addFilterPredicate(paramMap, Keys.CATEGORY, Entity.CATEGORY, Category.class, root, cb, predicates);
                addFilterPredicate(paramMap, Keys.FASHION_STYLE, Entity.FASHION_STYLE, FashionStyle.class, root, cb, predicates);
                addFilterPredicate(paramMap, Keys.OCCASION, Entity.OCCASION, Occasion.class, root, cb, predicates);
            } catch (IllegalArgumentException ex) {
                throw CatalogExceptionStore.INVALID_FILTER.exception();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepo.findAll(spec, pageable).map(this::convertFromEntity);
    }

    private void addFilterPredicate(Map<String, String> paramMap, String paramKey, 
            String entityField, Class<?> enumClass, jakarta.persistence.criteria.Root<ProductEntity> root, 
            jakarta.persistence.criteria.CriteriaBuilder cb, List<Predicate> predicates) {
        
        for (String key : paramMap.keySet()) {
            if (key.toLowerCase().startsWith(paramKey.toLowerCase() + ".")) {
                String values = key.substring(key.indexOf(".") + 1);
                String[] valueArray = values.split(",");
                List<Integer> codes = new ArrayList<>();
                
                for (String value : valueArray) {
                    value = value.trim();
                    if (!value.isEmpty()) {
                        int code = getEnumCode(enumClass, value);
                        codes.add(code);
                    }
                }
                
                if (!codes.isEmpty()) {
                    if (codes.size() == 1) {
                        predicates.add(cb.equal(root.get(entityField), codes.get(0)));
                    } else {
                        predicates.add(root.get(entityField).in(codes));
                    }
                }
                return;
            }
        }
    }

    private int getEnumCode(Class<?> enumClass, String name) {
        try {
            if (enumClass == Brand.class) return Brand.getCode(name);
            if (enumClass == Category.class) return Category.getCode(name);
            if (enumClass == FashionStyle.class) return FashionStyle.getCode(name);
            if (enumClass == Occasion.class) return Occasion.getCode(name);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid filter value: " + name);
        }
        throw new IllegalArgumentException("Unknown enum class: " + enumClass.getName());
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        try {
            // Create main product entity
            ProductEntity entity = ProductEntity.builder()
                    .name(request.getName())
                    .tagline(request.getTagline())
                    .sellingPrice(request.getSellingPrice())
                    .originalPrice(request.getOriginalPrice())
                    .discountPercentage(request.getDiscountPercentage())
                    .rating(BigDecimal.ZERO)
                    .ratingCount(0)
                    .brand(Brand.getCode(request.getBrand()))
                    .fashionStyle(FashionStyle.getCode(request.getFashionStyle()))
                    .category(Category.getCode(request.getCategory()))
                    .occasion(Occasion.getCode(request.getOccasion()))
                    .build();
            productRepo.save(entity);
            
            Long productId = entity.getProductId();
            
            // Create product details if provided
            if (request.getProductDetails() != null) {
                var detailsRequest = request.getProductDetails();
                var detailsEntity = com.shopkart.catalog.dto.ProductDetailsEntity.builder()
                        .productId(productId)
                        .description(detailsRequest.getDescription())
                        .color(detailsRequest.getColor())
                        .material(detailsRequest.getMaterial())
                        .length(detailsRequest.getLength())
                        .sleeve(detailsRequest.getSleeve())
                        .transparency(detailsRequest.getTransparency())
                        .careInstructions(detailsRequest.getCareInstructions())
                        .rating1Star(0)
                        .rating2Star(0)
                        .rating3Star(0)
                        .rating4Star(0)
                        .rating5Star(0)
                        .build();
                productDetailsRepo.save(detailsEntity);
            }
            
            // Create product stock entries
            if (request.getStock() != null && !request.getStock().isEmpty()) {
                for (Map.Entry<String, Integer> entry : request.getStock().entrySet()) {
                    String sizeName = entry.getKey();
                    Integer quantity = entry.getValue();
                    int sizeCode = Size.getCode(sizeName);
                    
                    var stockEntity = com.shopkart.catalog.dto.ProductStockEntity.builder()
                            .productId(productId)
                            .size(sizeCode)
                            .quantity(quantity)
                            .build();
                    productStockRepo.save(stockEntity);
                }
            }
            
            // Create product images
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                long currentTime = System.currentTimeMillis();
                for (int i = 0; i < request.getImages().size(); i++) {
                    String imageUrl = request.getImages().get(i);
                    var imageEntity = com.shopkart.catalog.dto.ProductImageEntity.builder()
                            .productId(productId)
                            .imageUrl(imageUrl)
                            .isThumbnail(i == 0)
                            .displayOrder(i + 1)
                            .createdTime(currentTime)
                            .build();
                    productImageRepo.save(imageEntity);
                }
            }
            
            return convertFromEntity(entity);
        } catch (IllegalArgumentException ex) {
            throw CatalogExceptionStore.INVALID_FILTER.exception();
        }
    }

    private ProductDTO convertFromEntity(ProductEntity entity) {
        // Fetch thumbnail image
        String thumbnailUrl = productImageRepo
                .findByProductIdAndIsThumbnail(entity.getProductId(), true)
                .map(com.shopkart.catalog.dto.ProductImageEntity::getImageUrl)
                .orElse(null);
        
        return ProductDTO.builder()
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
            .image(thumbnailUrl)
            .build();
    }
    
    public Map<String, Object> getProductDetails(Long productId) {
        // Fetch product entity
        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(() -> CatalogExceptionStore.PRODUCT_NOT_FOUND.exception());
        
        // Fetch product details
        com.shopkart.catalog.dto.ProductDetailsEntity details = productDetailsRepo
                .findByProductId(productId)
                .orElse(null);
        
        // Fetch all images ordered by display order
        List<String> images = productImageRepo.findByProductIdOrderByDisplayOrder(productId)
                .stream()
                .map(com.shopkart.catalog.dto.ProductImageEntity::getImageUrl)
                .toList();
        
        // Fetch stock by size
        Map<String, Integer> stock = new java.util.LinkedHashMap<>();
        productStockRepo.findByProductId(productId).forEach(stockEntity -> {
            String sizeName = Size.getName(stockEntity.getSize());
            stock.put(sizeName, stockEntity.getQuantity());
        });
        
        // Build response
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put(Keys.PRODUCT_ID, product.getProductId());
        response.put(Keys.NAME, product.getName());
        response.put("tagline", product.getTagline());
        response.put(Keys.SELLING_PRICE, product.getSellingPrice());
        response.put(Keys.ORIGINAL_PRICE, product.getOriginalPrice());
        response.put(Keys.DISCOUNT_PERCENTAGE, product.getDiscountPercentage());
        response.put(Keys.RATING, product.getRating());
        response.put(Keys.RATING_COUNT, product.getRatingCount());
        response.put(Keys.BRAND, Brand.getName(product.getBrand()));
        response.put(Keys.FASHION_STYLE, FashionStyle.getName(product.getFashionStyle()));
        response.put(Keys.CATEGORY, Category.getName(product.getCategory()));
        response.put(Keys.OCCASION, Occasion.getName(product.getOccasion()));
        
        // Add details if present
        if (details != null) {
            Map<String, Object> detailsMap = new java.util.LinkedHashMap<>();
            detailsMap.put("description", details.getDescription());
            detailsMap.put("color", details.getColor());
            detailsMap.put("material", details.getMaterial());
            detailsMap.put("length", details.getLength());
            detailsMap.put("sleeve", details.getSleeve());
            detailsMap.put("transparency", details.getTransparency());
            detailsMap.put("care_instructions", details.getCareInstructions());
            
            // Add rating breakdown
            Map<String, Integer> ratingBreakdown = new java.util.LinkedHashMap<>();
            ratingBreakdown.put("5_star", details.getRating5Star());
            ratingBreakdown.put("4_star", details.getRating4Star());
            ratingBreakdown.put("3_star", details.getRating3Star());
            ratingBreakdown.put("2_star", details.getRating2Star());
            ratingBreakdown.put("1_star", details.getRating1Star());
            
            // Calculate and add total_count
            int totalCount = details.getRating5Star() + details.getRating4Star() + 
                           details.getRating3Star() + details.getRating2Star() + 
                           details.getRating1Star();
            ratingBreakdown.put("total_count", totalCount);
            
            detailsMap.put("rating_breakdown", ratingBreakdown);
            
            response.put("details", detailsMap);
        }
        
        response.put("images", images);
        response.put("stock", stock);
        
        return response;
    }
}
