package com.shopkart.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public final class PageRequestBuilder {

    private PageRequestBuilder() {}

    public static Pageable build(Map<String, String> params, String defaultSortKey, Map<String, String> sortFieldMap) {
        int page = parseIntOrDefault(params.get(PageConstants.Params.PAGE), PageConstants.DEFAULT_PAGE);
        int size = parseIntOrDefault(params.get(PageConstants.Params.PAGE_SIZE), PageConstants.DEFAULT_SIZE);

        if (page < 0) page = PageConstants.DEFAULT_PAGE;
        if (size < 1 || size > PageConstants.MAX_SIZE) size = PageConstants.DEFAULT_SIZE;

        String sortKey = params.getOrDefault(PageConstants.Params.SORT_BY, defaultSortKey);
        if (!sortFieldMap.containsKey(sortKey)) {
            sortKey = defaultSortKey;
        }
        String entityField = sortFieldMap.get(sortKey);

        String sortOrder = params.getOrDefault(PageConstants.Params.SORT_ORDER, PageConstants.DEFAULT_SORT_ORDER);
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(entityField).descending()
                : Sort.by(entityField).ascending();

        return PageRequest.of(page, size, sort);
    }

    private static int parseIntOrDefault(String value, int defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
