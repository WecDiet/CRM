package com.CRM.service.Cloudinary;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

    // Map<String, Object> uploadMedia(MultipartFile media, String folderName, int
    // width,
    // int height);

    // void deleteMedia(String publicId);

    // File saveToTemp(MultipartFile media);

    CompletableFuture<Map<String, Object>> uploadMedia(MultipartFile media, String folderName, int width, int height,
            int quality);

    CompletableFuture<Void> deleteMedia(String publicId);
}
