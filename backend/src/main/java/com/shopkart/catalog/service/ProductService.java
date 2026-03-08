package com.shopkart.catalog.service;

import com.shopkart.catalog.dto.request.AddStockRequest;
import com.shopkart.catalog.dto.request.CreateProductRequest;
import com.shopkart.catalog.dto.request.ProductDetailsRequest;
import com.shopkart.catalog.dto.request.RateProductRequest;
import com.shopkart.catalog.dto.request.UpdateProductRequest;
import com.shopkart.catalog.model.ProductDetailsEntity;
import com.shopkart.catalog.model.ProductEntity;
import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.model.ProductImageEntity;
import com.shopkart.catalog.model.ProductStockEntity;
import com.shopkart.catalog.dto.enums.Brand;
import com.shopkart.catalog.dto.enums.Category;
import com.shopkart.catalog.dto.enums.FashionStyle;
import com.shopkart.catalog.dto.enums.Occasion;
import com.shopkart.catalog.dto.enums.Size;
import com.shopkart.catalog.repo.ProductDetailsRepo;
import com.shopkart.catalog.repo.ProductImageRepo;
import com.shopkart.catalog.repo.ProductRepo;
import com.shopkart.catalog.repo.ProductStockRepo;
import com.shopkart.catalog.util.CatalogConstants;
import com.shopkart.catalog.util.CatalogConstants.Entity;
import com.shopkart.catalog.util.CatalogConstants.Keys;
import com.shopkart.catalog.util.CatalogExceptionStore;
import com.shopkart.catalog.util.ProductFormatHandler;
import com.shopkart.common.util.Constants;
import com.shopkart.common.util.CurrencyUtil;
import com.shopkart.common.util.PageRequestBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductDetailsRepo productDetailsRepo;
    private final ProductImageRepo productImageRepo;
    private final ProductStockRepo productStockRepo;

    public Page<ProductResponse> getProducts(Map<String, String> paramMap) {
        Pageable pageable = PageRequestBuilder.build(paramMap, CatalogConstants.DEFAULT_SORT_KEY, CatalogConstants.SORT_FIELD_MAP);

        Specification<ProductEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            try {
                addFilterPredicate(paramMap, Keys.BRAND, Entity.BRAND, Brand.class, root, cb, predicates);
                addFilterPredicate(paramMap, Keys.CATEGORY, Entity.CATEGORY, Category.class, root, cb, predicates);
                addFilterPredicate(paramMap, Keys.FASHION_STYLE, Entity.FASHION_STYLE, FashionStyle.class, root, cb, predicates);
                addFilterPredicate(paramMap, Keys.OCCASION, Entity.OCCASION, Occasion.class, root, cb, predicates);
            } catch(IllegalArgumentException ex) {
                throw CatalogExceptionStore.INVALID_FILTER.exception();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepo.findAll(spec, pageable).map(this::convertFromEntity);
    }

    private void addFilterPredicate(Map<String, String> paramMap, String paramKey,
                                    String entityField, Class<?> enumClass, jakarta.persistence.criteria.Root<ProductEntity> root,
                                    jakarta.persistence.criteria.CriteriaBuilder cb, List<Predicate> predicates) {

        for(String key : paramMap.keySet()) {
            if(key.toLowerCase().startsWith(paramKey.toLowerCase() + ".")) {
                String values = key.substring(key.indexOf(".") + 1);
                String[] valueArray = values.split(",");
                List<Integer> codes = new ArrayList<>();

                for(String value : valueArray) {
                    value = value.trim();
                    if(!value.isEmpty()) {
                        int code = getEnumCode(enumClass, value);
                        codes.add(code);
                    }
                }

                if(!codes.isEmpty()) {
                    if(codes.size() == 1) {
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
            if(enumClass == Brand.class) return Brand.getCode(name);
            if(enumClass == Category.class) return Category.getCode(name);
            if(enumClass == FashionStyle.class) return FashionStyle.getCode(name);
            if(enumClass == Occasion.class) return Occasion.getCode(name);
        } catch(Exception e) {
            throw new IllegalArgumentException("Invalid filter value: " + name);
        }
        throw new IllegalArgumentException("Unknown enum class: " + enumClass.getName());
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        try {
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

            if(request.getProductDetails() != null) {
                ProductDetailsRequest detailsRequest = request.getProductDetails();
                ProductDetailsEntity detailsEntity = ProductDetailsEntity.builder()
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

            if(request.getStock() != null && !request.getStock().isEmpty()) {
                for(Map.Entry<String, Integer> entry : request.getStock().entrySet()) {
                    String sizeName = entry.getKey();
                    Integer quantity = entry.getValue();
                    int sizeCode = Size.getCode(sizeName);

                    ProductStockEntity stockEntity = ProductStockEntity.builder()
                            .productId(productId)
                            .size(sizeCode)
                            .quantity(quantity)
                            .build();
                    productStockRepo.save(stockEntity);
                }
            }

            if(request.getImages() != null && !request.getImages().isEmpty()) {
                long currentTime = System.currentTimeMillis();
                for(int i = 0; i < request.getImages().size(); i++) {
                    String imageUrl = request.getImages().get(i);
                    ProductImageEntity imageEntity = ProductImageEntity.builder()
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
        } catch(IllegalArgumentException ex) {
            throw CatalogExceptionStore.INVALID_FILTER.exception();
        }
    }

    @Transactional
    public Map<String, Object> updateProduct(Long productId, UpdateProductRequest request) {
        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        try {
            if(request.getName() != null) {
                product.setName(request.getName());
            }
            if(request.getTagline() != null) {
                product.setTagline(request.getTagline());
            }
            if(request.getSellingPrice() != null) {
                product.setSellingPrice(request.getSellingPrice());
            }
            if(request.getOriginalPrice() != null) {
                product.setOriginalPrice(request.getOriginalPrice());
            }
            if(request.getDiscountPercentage() != null) {
                product.setDiscountPercentage(request.getDiscountPercentage());
            }
            if(request.getBrand() != null) {
                product.setBrand(Brand.getCode(request.getBrand()));
            }
            if(request.getFashionStyle() != null) {
                product.setFashionStyle(FashionStyle.getCode(request.getFashionStyle()));
            }
            if(request.getCategory() != null) {
                product.setCategory(Category.getCode(request.getCategory()));
            }
            if(request.getOccasion() != null) {
                product.setOccasion(Occasion.getCode(request.getOccasion()));
            }

            if((request.getSellingPrice() != null || request.getOriginalPrice() != null)
                    && request.getDiscountPercentage() == null) {
                BigDecimal selling = product.getSellingPrice();
                BigDecimal original = product.getOriginalPrice();
                if(original.compareTo(BigDecimal.ZERO) > 0) {
                    int discount = original.subtract(selling)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(original, 0, RoundingMode.HALF_UP)
                            .intValue();
                    product.setDiscountPercentage(discount);
                }
            }

            productRepo.save(product);

            if(request.getProductDetails() != null) {
                ProductDetailsRequest detailsRequest = request.getProductDetails();
                ProductDetailsEntity details = productDetailsRepo.findByProductId(productId).orElse(null);

                if(details == null) {
                    details = ProductDetailsEntity.builder()
                            .productId(productId)
                            .build();
                }

                if(detailsRequest.getDescription() != null) {
                    details.setDescription(detailsRequest.getDescription());
                }
                if(detailsRequest.getColor() != null) {
                    details.setColor(detailsRequest.getColor());
                }
                if(detailsRequest.getMaterial() != null) {
                    details.setMaterial(detailsRequest.getMaterial());
                }
                if(detailsRequest.getLength() != null) {
                    details.setLength(detailsRequest.getLength());
                }
                if(detailsRequest.getSleeve() != null) {
                    details.setSleeve(detailsRequest.getSleeve());
                }
                if(detailsRequest.getTransparency() != null) {
                    details.setTransparency(detailsRequest.getTransparency());
                }
                if(detailsRequest.getCareInstructions() != null) {
                    details.setCareInstructions(detailsRequest.getCareInstructions());
                }

                productDetailsRepo.save(details);
            }
        } catch(IllegalArgumentException ex) {
            throw CatalogExceptionStore.INVALID_FILTER.exception();
        }

        return getProductDetails(productId);
    }

    private ProductResponse convertFromEntity(ProductEntity entity) {
        String thumbnailUrl = productImageRepo
                .findByProductIdAndIsThumbnail(entity.getProductId(), true)
                .map(ProductImageEntity::getImageUrl)
                .orElse(null);

        return ProductResponse.builder()
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
        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        ProductDetailsEntity details = productDetailsRepo
                .findByProductId(productId)
                .orElse(null);

        List<String> images = productImageRepo.findByProductIdOrderByDisplayOrder(productId)
                .stream()
                .map(ProductImageEntity::getImageUrl)
                .toList();

        Map<String, Integer> stock = new java.util.LinkedHashMap<>();
        productStockRepo.findByProductId(productId).forEach(stockEntity -> {
            String sizeName = Size.getName(stockEntity.getSize());
            stock.put(sizeName, stockEntity.getQuantity());
        });

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put(Keys.PRODUCT_ID, product.getProductId());
        response.put(Keys.NAME, product.getName());
        response.put(Keys.TAG_LINE, product.getTagline());
        response.put(Keys.SELLING_PRICE, product.getSellingPrice());
        response.put(Keys.SELLING_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getSellingPrice()));
        response.put(Keys.ORIGINAL_PRICE, product.getOriginalPrice());
        response.put(Keys.ORIGINAL_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getOriginalPrice()));
        response.put(Keys.DISCOUNT_PERCENTAGE, product.getDiscountPercentage());
        response.put(Keys.DISCOUNT_PERCENTAGE + Keys.FORMATTED_SUFFIX, product.getDiscountPercentage() != null ? product.getDiscountPercentage() + Constants.Symbols.PERCENT_OFF : null);
        response.put(Keys.RATING, product.getRating());
        response.put(Keys.RATING + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatRatingDisplay(product.getRating()));
        response.put(Keys.RATING_COUNT, product.getRatingCount());
        response.put(Keys.RATING_COUNT + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatCustomerCount(product.getRatingCount()));
        response.put(Keys.BRAND, Brand.getName(product.getBrand()));
        response.put(Keys.BRAND + Keys.FORMATTED_SUFFIX, Brand.getDisplayName(product.getBrand()));
        response.put(Keys.FASHION_STYLE, FashionStyle.getName(product.getFashionStyle()));
        response.put(Keys.FASHION_STYLE + Keys.FORMATTED_SUFFIX, FashionStyle.getDisplayName(product.getFashionStyle()));
        response.put(Keys.CATEGORY, Category.getName(product.getCategory()));
        response.put(Keys.CATEGORY + Keys.FORMATTED_SUFFIX, Category.getDisplayName(product.getCategory()));
        response.put(Keys.OCCASION, Occasion.getName(product.getOccasion()));
        response.put(Keys.OCCASION + Keys.FORMATTED_SUFFIX, Occasion.getDisplayName(product.getOccasion()));

        if(details != null) {
            Map<String, Object> detailsMap = new java.util.LinkedHashMap<>();
            detailsMap.put(Keys.DESCRIPTION, details.getDescription());
            detailsMap.put(Keys.COLOR, details.getColor());
            detailsMap.put(Keys.MATERIAL, details.getMaterial());
            detailsMap.put(Keys.LENGTH, details.getLength());
            detailsMap.put(Keys.SLEEVE, details.getSleeve());
            detailsMap.put(Keys.TRANSPARENCY, details.getTransparency());
            detailsMap.put(Keys.CARE_INSTRUCTIONS, details.getCareInstructions());

            Map<String, Object> ratingBreakdown = new java.util.LinkedHashMap<>();

            int totalCount = details.getRating5Star() + details.getRating4Star() + details.getRating3Star() + details.getRating2Star() + details.getRating1Star();

            ratingBreakdown.put(Keys.STAR_5, details.getRating5Star());
            ratingBreakdown.put(Keys.STAR_5 + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatStarPercentage(details.getRating5Star(), totalCount));
            ratingBreakdown.put(Keys.STAR_4, details.getRating4Star());
            ratingBreakdown.put(Keys.STAR_4 + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatStarPercentage(details.getRating4Star(), totalCount));
            ratingBreakdown.put(Keys.STAR_3, details.getRating3Star());
            ratingBreakdown.put(Keys.STAR_3 + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatStarPercentage(details.getRating3Star(), totalCount));
            ratingBreakdown.put(Keys.STAR_2, details.getRating2Star());
            ratingBreakdown.put(Keys.STAR_2 + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatStarPercentage(details.getRating2Star(), totalCount));
            ratingBreakdown.put(Keys.STAR_1, details.getRating1Star());
            ratingBreakdown.put(Keys.STAR_1 + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatStarPercentage(details.getRating1Star(), totalCount));

            ratingBreakdown.put(Keys.TOTAL_COUNT, totalCount);
            ratingBreakdown.put(Keys.TOTAL_COUNT + Keys.FORMATTED_SUFFIX, ProductFormatHandler.formatCustomerCount(totalCount));

            detailsMap.put(Keys.RATING_BREAKDOWN, ratingBreakdown);

            response.put(Keys.DETAILS, detailsMap);
        }

        response.put(Keys.IMAGES, images);
        response.put(Keys.STOCK, stock);

        return response;
    }

    public Map<String, Object> getProductStock(Long productId) {
        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        List<ProductStockEntity> stockEntities = productStockRepo.findByProductIdOrderBySize(productId);

        List<Map<String, Object>> stockList = new ArrayList<>();
        for(ProductStockEntity stockEntity : stockEntities) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put(Keys.SIZE, Size.getName(stockEntity.getSize()));
            entry.put(Keys.SIZE + Keys.FORMATTED_SUFFIX, Size.getDisplayName(stockEntity.getSize()));
            entry.put(Keys.QUANTITY, stockEntity.getQuantity());
            stockList.add(entry);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.PRODUCT_ID, product.getProductId());
        response.put(Keys.PRODUCT_NAME, product.getName());
        response.put(Keys.STOCK, stockList);
        return response;
    }

    @Transactional
    public Map<String, Object> addStock(Long productId, AddStockRequest request) {
        productRepo.findById(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        for(Map.Entry<String, Integer> entry : request.getStock().entrySet()) {
            String sizeName = entry.getKey();
            Integer quantityToAdd = entry.getValue();
            int sizeCode = Size.getCode(sizeName);

            ProductStockEntity stockEntity = productStockRepo
                    .findByProductIdAndSize(productId, sizeCode)
                    .orElse(null);

            if(stockEntity != null) {
                stockEntity.setQuantity(stockEntity.getQuantity() + quantityToAdd);
            } else {
                stockEntity = ProductStockEntity.builder()
                        .productId(productId)
                        .size(sizeCode)
                        .quantity(quantityToAdd)
                        .build();
            }
            productStockRepo.save(stockEntity);
        }

        return getProductStock(productId);
    }

    public Map<String, Object> getOutOfStockProducts() {
        List<Long> oosProductIds = productStockRepo.findProductIdsWithNoStock();
        List<ProductEntity> products = productRepo.findAllById(oosProductIds);

        List<Map<String, Object>> productList = new ArrayList<>();
        for(ProductEntity product : products) {
            String thumbnail = productImageRepo
                    .findByProductIdAndIsThumbnail(product.getProductId(), true)
                    .map(ProductImageEntity::getImageUrl)
                    .orElse(null);

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put(Keys.PRODUCT_ID, product.getProductId());
            entry.put(Keys.NAME, product.getName());
            entry.put(Keys.BRAND + Keys.FORMATTED_SUFFIX, Brand.getDisplayName(product.getBrand()));
            entry.put(Keys.SELLING_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getSellingPrice()));
            entry.put(Keys.IMAGE, thumbnail);
            productList.add(entry);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.PRODUCTS, productList);
        response.put(Keys.TOTAL, productList.size());
        return response;
    }

    @Transactional
    public Map<String, Object> rateProduct(Long userId, Long productId, RateProductRequest request) {
        if(request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw CatalogExceptionStore.INVALID_RATING.exception();
        }

        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        ProductDetailsEntity productDetails = productDetailsRepo.findByProductId(productId)
                .orElseThrow(CatalogExceptionStore.PRODUCT_NOT_FOUND::exception);

        switch(request.getRating()) {
            case 1 -> productDetails.setRating1Star(productDetails.getRating1Star() + 1);
            case 2 -> productDetails.setRating2Star(productDetails.getRating2Star() + 1);
            case 3 -> productDetails.setRating3Star(productDetails.getRating3Star() + 1);
            case 4 -> productDetails.setRating4Star(productDetails.getRating4Star() + 1);
            case 5 -> productDetails.setRating5Star(productDetails.getRating5Star() + 1);
        }

        Integer totalCount = productDetails.getRating1Star() + productDetails.getRating2Star() + productDetails.getRating3Star() + productDetails.getRating4Star() + productDetails.getRating5Star();
        BigDecimal averageRating = BigDecimal.valueOf((productDetails.getRating1Star() + productDetails.getRating2Star() * 2 + productDetails.getRating3Star() * 3 + productDetails.getRating4Star() * 4 + productDetails.getRating5Star() * 5) / (double) totalCount).setScale(1, RoundingMode.HALF_UP);

        product.setRatingCount(totalCount);
        product.setRating(averageRating);
        productRepo.save(product);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.PRODUCT_ID, productId);
        response.put(Keys.RATING, request.getRating());
        response.put(Keys.AVERAGE_RATING, averageRating);
        response.put(Keys.RATING_COUNT, totalCount);
        return response;
    }
}
