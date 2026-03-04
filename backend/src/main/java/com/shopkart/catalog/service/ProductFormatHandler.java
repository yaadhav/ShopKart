package com.shopkart.catalog.service;

import com.shopkart.catalog.model.ProductDTO;
import com.shopkart.catalog.model.enums.Brand;
import com.shopkart.catalog.model.enums.Category;
import com.shopkart.catalog.model.enums.FashionStyle;
import com.shopkart.catalog.model.enums.Occasion;
import com.shopkart.catalog.model.enums.Size;
import com.shopkart.catalog.util.CatalogConstants.Keys;
import com.shopkart.common.util.Constants;
import com.shopkart.common.util.CurrencyUtil;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductFormatHandler {

    public static Map<String, Object> addFormattedNodes(ProductDTO product) {
        Map<String, Object> formatted = new LinkedHashMap<>();
        formatted.put(Keys.SELLING_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getSellingPrice()));
        formatted.put(Keys.ORIGINAL_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getOriginalPrice()));
        formatted.put(Keys.DISCOUNT_PERCENTAGE + Keys.FORMATTED_SUFFIX, product.getDiscountPercentage() != null ? product.getDiscountPercentage() + Constants.Symbols.PERCENT_OFF : null);
        formatted.put(Keys.RATING + Keys.FORMATTED_SUFFIX, product.getRating() != null ? product.getRating() + Constants.Symbols.RATING_SUFFIX : null);
        formatted.put(Keys.BRAND + Keys.FORMATTED_SUFFIX, Brand.getDisplayName(Brand.getCode(product.getBrand())));
        formatted.put(Keys.CATEGORY + Keys.FORMATTED_SUFFIX, Category.getDisplayName(Category.getCode(product.getCategory())));
        formatted.put(Keys.FASHION_STYLE + Keys.FORMATTED_SUFFIX, FashionStyle.getDisplayName(FashionStyle.getCode(product.getFashionStyle())));
        formatted.put(Keys.OCCASION + Keys.FORMATTED_SUFFIX, Occasion.getDisplayName(Occasion.getCode(product.getOccasion())));
        formatted.put(Keys.SIZE + Keys.FORMATTED_SUFFIX, Size.getDisplayName(Size.getCode(product.getSize())));
        return formatted;
    }
}
