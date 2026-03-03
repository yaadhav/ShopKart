package com.shopkart.catalog.controller;

import com.shopkart.catalog.dto.MetaItem;
import com.shopkart.catalog.dto.MetaResponse;
import com.shopkart.catalog.model.enums.Brand;
import com.shopkart.catalog.model.enums.Category;
import com.shopkart.catalog.model.enums.FashionStyle;
import com.shopkart.catalog.model.enums.Occasion;
import com.shopkart.catalog.model.enums.Size;
import com.shopkart.catalog.util.CatalogConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/meta")
public class MetaAPI {

    @GetMapping("")
    public ResponseEntity<MetaResponse> getAll() {
        MetaResponse response = MetaResponse.builder()
                .brands(toMetaList(Brand.values()))
                .categories(toMetaList(Category.values()))
                .fashionStyles(toMetaList(FashionStyle.values()))
                .occasions(toMetaList(Occasion.values()))
                .sizes(toMetaList(Size.values()))
                .sortableFields(toSortMetaList())
                .build();
        return ResponseEntity.ok(response);
    }

    private <E extends Enum<E>> List<MetaItem> toMetaList(E[] values) {
        return Arrays.stream(values)
                .map(e -> {
                    try {
                        String name = (String) e.getClass().getField("name").get(e);
                        String displayName = (String) e.getClass().getField("displayName").get(e);
                        return new MetaItem(name, displayName);
                    } catch (ReflectiveOperationException ex) {
                        throw new IllegalStateException("Enum missing expected fields", ex);
                    }
                })
                .toList();
    }

    private List<MetaItem> toSortMetaList() {
        return CatalogConstants.SORT_LABEL_MAP.entrySet().stream()
                .map(e -> new MetaItem(e.getKey(), e.getValue()))
                .toList();
    }
}
