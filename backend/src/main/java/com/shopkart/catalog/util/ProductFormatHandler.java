package com.shopkart.catalog.util;

import com.shopkart.catalog.dto.response.ProductResponse;
import com.shopkart.catalog.dto.enums.Brand;
import com.shopkart.catalog.dto.enums.Category;
import com.shopkart.catalog.dto.enums.FashionStyle;
import com.shopkart.catalog.dto.enums.Occasion;
import com.shopkart.catalog.util.CatalogConstants.Keys;
import com.shopkart.common.util.Constants;
import com.shopkart.common.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductFormatHandler {

    public static Map<String, Object> addFormattedNodes(ProductResponse product) {
        Map<String, Object> formatted = new LinkedHashMap<>();
        formatted.put(Keys.SELLING_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getSellingPrice()));
        formatted.put(Keys.ORIGINAL_PRICE + Keys.FORMATTED_SUFFIX, CurrencyUtil.formatWithINR(product.getOriginalPrice()));
        formatted.put(Keys.DISCOUNT_PERCENTAGE + Keys.FORMATTED_SUFFIX, product.getDiscountPercentage() != null ? product.getDiscountPercentage() + Constants.Symbols.PERCENT_OFF : null);
        formatted.put(Keys.RATING + Keys.FORMATTED_SUFFIX, product.getRating() != null ? product.getRating() + Constants.Symbols.RATING_SUFFIX : null);
        formatted.put(Keys.BRAND + Keys.FORMATTED_SUFFIX, Brand.getDisplayName(Brand.getCode(product.getBrand())));
        formatted.put(Keys.CATEGORY + Keys.FORMATTED_SUFFIX, Category.getDisplayName(Category.getCode(product.getCategory())));
        formatted.put(Keys.FASHION_STYLE + Keys.FORMATTED_SUFFIX, FashionStyle.getDisplayName(FashionStyle.getCode(product.getFashionStyle())));
        formatted.put(Keys.OCCASION + Keys.FORMATTED_SUFFIX, Occasion.getDisplayName(Occasion.getCode(product.getOccasion())));
        return formatted;
    }

    public static String formatRatingDisplay(BigDecimal rating) {
        if (rating == null) return null;
        return rating.setScale(1, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    public static String formatCustomerCount(int total) {
        return total + " Customers";
    }

    public static String formatStarPercentage(int starCount, int total) {
        if (total == 0) return "0%";
        return Math.round(starCount * 100.0 / total) + Constants.Symbols.PERCENT;
    }
}
