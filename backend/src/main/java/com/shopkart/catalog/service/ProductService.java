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

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
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
            ProductEntity entity = ProductEntity.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .sellingPrice(request.getSellingPrice())
                    .originalPrice(request.getOriginalPrice())
                    .discountPercentage(request.getDiscountPercentage())
                    .rating(BigDecimal.ZERO)
                    .ratingCount(0)
                    .brand(Brand.getCode(request.getBrand()))
                    .fashionStyle(FashionStyle.getCode(request.getFashionStyle()))
                    .category(Category.getCode(request.getCategory()))
                    .occasion(Occasion.getCode(request.getOccasion()))
                    .size(Size.getCode(request.getSize()))
                    .image(request.getImage())
                    .build();
            productRepo.save(entity);
            return convertFromEntity(entity);
        } catch (IllegalArgumentException ex) {
            throw CatalogExceptionStore.INVALID_FILTER.exception();
        }
    }

    private ProductDTO convertFromEntity(ProductEntity entity) {
        return ProductDTO.builder()
            .productId(entity.getProductId())
            .name(entity.getName())
            .description(entity.getDescription())
            .sellingPrice(entity.getSellingPrice())
            .originalPrice(entity.getOriginalPrice())
            .discountPercentage(entity.getDiscountPercentage())
            .rating(entity.getRating())
            .ratingCount(entity.getRatingCount())
            .brand(Brand.getName(entity.getBrand()))
            .fashionStyle(FashionStyle.getName(entity.getFashionStyle()))
            .category(Category.getName(entity.getCategory()))
            .occasion(Occasion.getName(entity.getOccasion()))
            .size(Size.getName(entity.getSize()))
            .image(entity.getImage())
            .build();
    }
}
