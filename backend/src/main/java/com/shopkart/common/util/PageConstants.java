package com.shopkart.common.util;

public final class PageConstants {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_SORT_ORDER = "asc";

    public static final class Params {
        public static final String PAGE = "page";
        public static final String PAGE_SIZE = "page_size";
        public static final String SORT_BY = "sort_by";
        public static final String SORT_ORDER = "sort_order";
    }

    public static final class Keys {
        public static final String ITEMS = "items";
        public static final String PAGE = "page";
        public static final String PAGE_SIZE = "page_size";
        public static final String TOTAL_ITEMS = "total_items";
        public static final String TOTAL_PAGES = "total_pages";
    }
}
