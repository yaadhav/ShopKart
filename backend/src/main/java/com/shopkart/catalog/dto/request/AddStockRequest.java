package com.shopkart.catalog.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AddStockRequest {
    private Map<String, Integer> stock;
}
