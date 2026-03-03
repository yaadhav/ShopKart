package com.shopkart.catalog.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CatalogConstants {

    public static final class Entity {
        public static final String PRODUCT_ID = "productId";
        public static final String NAME = "name";
        public static final String SELLING_PRICE = "sellingPrice";
        public static final String RATING = "rating";
        public static final String DISCOUNT_PERCENTAGE = "discountPercentage";
        public static final String STOCK = "stock";
        public static final String FASHION_STYLE = "fashionStyle";
        public static final String CATEGORY = "category";
        public static final String BRAND = "brand";
        public static final String OCCASION = "occasion";
        public static final String SIZE = "size";
    }

    public static final class Keys {
        public static final String PRODUCT_ID = "product_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SELLING_PRICE = "selling_price";
        public static final String ORIGINAL_PRICE = "original_price";
        public static final String DISCOUNT_PERCENTAGE = "discount_percentage";
        public static final String RATING = "rating";
        public static final String BRAND = "brand";
        public static final String FASHION_STYLE = "fashion_style";
        public static final String CATEGORY = "category";
        public static final String OCCASION = "occasion";
        public static final String SIZE = "size";
        public static final String IMAGE = "image";
        public static final String STOCK = "stock";
        public static final String FORMATTED_SUFFIX = "_formatted";
    }

    public static final class SortLabels {
        public static final String DEFAULT = "Default";
        public static final String NAME = "Name";
        public static final String PRICE = "Price";
        public static final String RATING = "Rating";
        public static final String DISCOUNT = "Discount";
        public static final String STOCK = "Stock";
    }

    public static final String DEFAULT_SORT_KEY = Keys.PRODUCT_ID;

    public static final Map<String, String> SORT_FIELD_MAP;

    public static final Map<String, String> SORT_LABEL_MAP;

    static {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put(Keys.PRODUCT_ID, Entity.PRODUCT_ID);
        fields.put(Keys.NAME, Entity.NAME);
        fields.put(Keys.SELLING_PRICE, Entity.SELLING_PRICE);
        fields.put(Keys.RATING, Entity.RATING);
        fields.put(Keys.DISCOUNT_PERCENTAGE, Entity.DISCOUNT_PERCENTAGE);
        fields.put(Keys.STOCK, Entity.STOCK);
        SORT_FIELD_MAP = Collections.unmodifiableMap(fields);

        Map<String, String> labels = new LinkedHashMap<>();
        labels.put(Keys.PRODUCT_ID, SortLabels.DEFAULT);
        labels.put(Keys.NAME, SortLabels.NAME);
        labels.put(Keys.SELLING_PRICE, SortLabels.PRICE);
        labels.put(Keys.RATING, SortLabels.RATING);
        labels.put(Keys.DISCOUNT_PERCENTAGE, SortLabels.DISCOUNT);
        labels.put(Keys.STOCK, SortLabels.STOCK);
        SORT_LABEL_MAP = Collections.unmodifiableMap(labels);
    }
}
