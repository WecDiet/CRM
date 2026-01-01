package com.CRM.configuration.cloudinary;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    @Value("${cloudinary.api_key}")
    private String apiKey;
    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary getCloudinary() {
        /*
         * Tạo một đối tượng Cloudinary với các thông tin cấu hình cần thiết.
         * - Object: là giá trị có thể là bất kỳ kiểu dữ liệu nào — String, Boolean,
         * Integer, v.v.
         * - Map<String, Object>: là một bản đồ ánh xạ giữa tên thuộc tính và giá trị
         * của nó.
         * - "secure": true: Thuộc tính "secure" có vai trò bắt buộc Cloudinary sử dụng
         * HTTPS thay vì HTTP khi sinh ra các URL ảnh.
         * - "cloud_name", "api_key", "api_secret": là các thông tin cần thiết để xác
         * thực và kết nối với dịch vụ Cloudinary.
         */

        // Cách 1
        // Sử dụng các biến môi trường đã được cấu hình trong file
        // application.properties
        Map<String, Object> config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true);
        return new Cloudinary(config); // Trả về một đối tượng Cloudinary đã được cấu hình.
    }
}
