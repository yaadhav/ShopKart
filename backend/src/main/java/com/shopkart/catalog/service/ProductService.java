package com.shopkart.catalog.service;

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
            predicates.add(cb.greaterThan(root.get(Entity.STOCK), 0));
            try {
                if(paramMap.containsKey(Keys.FASHION_STYLE)) {
                    int fashionStyleCode = FashionStyle.getCode(paramMap.get(Keys.FASHION_STYLE));
                    predicates.add(cb.equal(root.get(Entity.FASHION_STYLE), fashionStyleCode));
                }
                if(paramMap.containsKey(Keys.CATEGORY)) {
                    int categoryCode = Category.getCode(paramMap.get(Keys.CATEGORY));
                    predicates.add(cb.equal(root.get(Entity.CATEGORY), categoryCode));
                }
                if(paramMap.containsKey(Keys.BRAND)) {
                    int brandCode = Brand.getCode(paramMap.get(Keys.BRAND));
                    predicates.add(cb.equal(root.get(Entity.BRAND), brandCode));
                }
                if(paramMap.containsKey(Keys.OCCASION)) {
                    int occasionCode = Occasion.getCode(paramMap.get(Keys.OCCASION));
                    predicates.add(cb.equal(root.get(Entity.OCCASION), occasionCode));
                }
                if(paramMap.containsKey(Keys.SIZE)) {
                    int sizeCode = Size.getCode(paramMap.get(Keys.SIZE));
                    predicates.add(cb.equal(root.get(Entity.SIZE), sizeCode));
                }
            } catch (IllegalArgumentException ex) {
                throw CatalogExceptionStore.INVALID_FILTER.exception();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepo.findAll(spec, pageable).map(this::convertFromEntity);
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
            .brand(Brand.getName(entity.getBrand()))
            .fashionStyle(FashionStyle.getName(entity.getFashionStyle()))
            .category(Category.getName(entity.getCategory()))
            .occasion(Occasion.getName(entity.getOccasion()))
            .size(Size.getName(entity.getSize()))
            .image(entity.getImage())
            .stock(entity.getStock())
            .build();
    }
}
