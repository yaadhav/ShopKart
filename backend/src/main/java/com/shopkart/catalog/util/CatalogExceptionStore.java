package com.shopkart.catalog.util;

import com.shopkart.common.exception.ExceptionStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CatalogExceptionStore implements ExceptionStore {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "CATALOG_001", "Product not found"),
    INVALID_FILTER(HttpStatus.BAD_REQUEST, "CATALOG_002", "Invalid filter parameter"),
    INVALID_BRAND(HttpStatus.BAD_REQUEST, "CATALOG_003", "Invalid brand"),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "CATALOG_004", "Invalid category"),
    INVALID_FASHION_STYLE(HttpStatus.BAD_REQUEST, "CATALOG_005", "Invalid fashion style"),
    INVALID_OCCASION(HttpStatus.BAD_REQUEST, "CATALOG_006", "Invalid occasion"),
    INVALID_SIZE(HttpStatus.BAD_REQUEST, "CATALOG_007", "Invalid size");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
