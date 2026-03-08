package com.shopkart.order.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OrderConstants {

    public static final class Keys {
        public static final String FORMATTED_SUFFIX = "_formatted";
        public static final String ORDER_ID = "order_id";
        public static final String USER_ID = "user_id";
        public static final String PAYMENT_ID = "payment_id";
        public static final String ORDER_AMOUNT = "order_amount";
        public static final String ORDER_SAVINGS = "order_savings";
        public static final String CONVENIENCE_FEE = "convenience_fee";
        public static final String ORDER_TOTAL = "order_total";
        public static final String PAYMENT_MODE = "payment_mode";
        public static final String PAYMENT_STATUS = "payment_status";
        public static final String ORDER_STATUS = "order_status";
        public static final String INITIATED_TIME = "initiated_time";
        public static final String DELIVERED_TIME = "delivered_time";
        public static final String PRODUCTS = "products";
        public static final String IMAGE_URL = "image_url";
        public static final String PRODUCT_NAME = "product_name";
        public static final String PRODUCT_ID = "product_id";
        public static final String SIZE = "size";
        public static final String QUANTITY = "quantity";
        public static final String SELLING_PRICE = "selling_price";
        public static final String ORIGINAL_PRICE = "original_price";
        public static final String SAVINGS = "savings";
        public static final String DELIVERY_FEE = "delivery_fee";
        public static final String PLATFORM_FEE = "platform_fee";
        public static final String ADDRESS = "address";
        public static final String ORDERS = "orders";
        public static final String PAYMENT_INTENT_ID = "payment_intent_id";
        public static final String USER_NAME = "user_name";
        public static final String NAME = "name";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String FIRST_LINE = "first_line";
        public static final String SECOND_LINE = "second_line";
        public static final String LANDMARK = "landmark";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String PINCODE = "pincode";
        public static final String STATUS = "status";
        public static final String PAYMENT_METHOD = "payment_method";
        public static final String REFERENCE_ID = "reference_id";
        public static final String FEE_DETAILS_ID = "fee_details_id";
        public static final String UPDATED_BY = "updated_by";
    }

    public static final class Entity {
        public static final String INITIATED_TIME = "initiatedTime";
        public static final String DELIVERED_TIME = "deliveredTime";
        public static final String ORDER_STATUS = "orderStatus";
        public static final String ORDER_ID = "orderId";
    }

    public static final class SortLabels {
        public static final String INITIATED_TIME = "initiated_time";
        public static final String DELIVERED_TIME = "delivered_time";
    }

    public static final Map<String, String> SORT_FIELD_MAP = new LinkedHashMap<>();

    static {
        SORT_FIELD_MAP.put(SortLabels.INITIATED_TIME, Entity.INITIATED_TIME);
        SORT_FIELD_MAP.put(SortLabels.DELIVERED_TIME, Entity.DELIVERED_TIME);
    }
}
