package com.shopkart.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

    private final Path uploadDir;

    public FileUploadService(@Value("${shopkart.upload.dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG and PNG images are allowed");
        }

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        Files.createDirectories(uploadDir);
        Files.copy(file.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private String getExtension(String originalName) {
        if (originalName == null) return ".jpg";
        int dot = originalName.lastIndexOf('.');
        return dot >= 0 ? originalName.substring(dot) : ".jpg";
    }
}
