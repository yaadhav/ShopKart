package com.shopkart.common.controller;

import com.shopkart.common.service.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/uploads")
public class UploadAPI {

    private final FileUploadService fileUploadService;

    public UploadAPI(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('admin', 'owner')")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileUploadService.store(file);
        String filePath = "/uploads/" + fileName;
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("file_path", filePath));
    }
}
