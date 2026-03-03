package com.shopkart.common.util;

import org.springframework.data.domain.Page;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PagedResponse {

    private PagedResponse() {}

    public static <T> Map<String, Object> from(Page<T> page, List<?> items) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(PageConstants.Keys.ITEMS, items);
        response.put(PageConstants.Keys.PAGE, page.getNumber());
        response.put(PageConstants.Keys.PAGE_SIZE, page.getSize());
        response.put(PageConstants.Keys.TOTAL_ITEMS, page.getTotalElements());
        response.put(PageConstants.Keys.TOTAL_PAGES, page.getTotalPages());
        return response;
    }
}
