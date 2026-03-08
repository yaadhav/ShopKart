package com.shopkart.payment.util;

public final class PaymentConstants {

    public static final class Keys {
        public static final String FORMATTED_SUFFIX = "_formatted";
        public static final String PAYMENT_ID = "payment_id";
        public static final String PAYMENT_INTENT_ID = "payment_intent_id";
        public static final String USER_ID = "user_id";
        public static final String ORDER_ID = "order_id";
        public static final String TOTAL_AMOUNT = "total_amount";
        public static final String PAYMENT_STATUS = "payment_status";
        public static final String PAYMENT_METHOD = "payment_method";
        public static final String PAYMENT_MODE = "payment_mode";
        public static final String REFERENCE_ID = "reference_id";
        public static final String SECRET_KEY = "secret_key";
        public static final String STATUS = "status";
        public static final String SUCCESS = "success";
        public static final String FAILED = "failed";
    }

    public static final class API {
        public static final String CREATE_PAYMENT_URL = "https://api.mockpaymentgateway.com/v1/payment/create";
    }

    public static final class Security {
        public static final String OUTBOUND_SECRET = "sk_live_ShopKart_9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c";
        public static final String INBOUND_SECRET = "sk_verify_ShopKart_1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d";
        public static final long PAYMENT_EXPIRY_MILLIS = 5 * 60 * 1000;
    }
}
