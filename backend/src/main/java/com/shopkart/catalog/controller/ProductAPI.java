package com.shopkart.catalog.controller;

import com.shopkart.catalog.dto.CreateProductRequest;
import com.shopkart.catalog.model.ProductDTO;
import com.shopkart.catalog.service.ProductService;
import com.shopkart.catalog.util.ProductUtil;
import com.shopkart.common.service.FileUploadService;
import com.shopkart.common.util.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductAPI {

    private final ProductService productService;
    private final FileUploadService fileUploadService;

    public ProductAPI(ProductService productService, FileUploadService fileUploadService) {
        this.productService = productService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getProducts(@RequestParam Map<String, String> filters) {
        Page<ProductDTO> page = productService.getProducts(filters);
        List<Map<String, Object>> items = page.getContent().stream()
                .map(ProductUtil::toResponse)
                .toList();
        return ResponseEntity.ok(PagedResponse.from(page, items));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('admin', 'owner')")
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestPart("product") @Valid CreateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileUploadService.store(imageFile);
            request.setImage("/uploads/" + fileName);
        }

        ProductDTO product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductUtil.toResponse(product));
    }
}
