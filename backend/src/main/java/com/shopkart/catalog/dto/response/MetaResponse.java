package com.shopkart.catalog.dto.response;

import com.shopkart.catalog.dto.resource.MetaItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MetaResponse {

    private List<MetaItem> brands;
    private List<MetaItem> categories;
    private List<MetaItem> fashionStyles;
    private List<MetaItem> occasions;
    private List<MetaItem> sizes;
    private List<MetaItem> sortableFields;
}
