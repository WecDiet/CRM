package com.CRM.service.Cloudinary;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    Map<String, Object> uploadMedia(MultipartFile media, String folderName, int width, int height);

    void deleteMedia(String publicId);
}
