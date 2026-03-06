package com.shopkart.common.util;

public final class Constants {

    public static final class Symbols {
        public static final String PERCENT_OFF = "% off";
        public static final String RATING_SUFFIX = "/5";
    }

    public static final class Jwt {
        public static final String ISSUER = "shopkart";
        public static final String CLAIM_EMAIL = "email";
        public static final String CLAIM_ROLE = "role";
    }

    public static final class Gender {
        public static final short NOT_SPECIFIED = 0;
        public static final short MALE = 1;
        public static final short FEMALE = 2;
        public static final short OTHER = 3;
    }

    public static final class Validation {
        public static final String PHONE_NUMBER_PATTERN = "^[0-9]{10,15}$";
        public static final String PINCODE_PATTERN = "^[0-9]{6}$";
    }
}
