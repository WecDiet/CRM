package com.CRM.service.Cloudinary;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.FileUploadUtils;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements ICloudinaryService {

    private final Cloudinary cloudinary;

    // @Override
    // public Map<String, Object> uploadMediaq243(MultipartFile media, String
    // folderName) {
    // try {
    // // Validate file
    // FileUploadUtils.assertAllowed(media, FileUploadUtils.MEDIA_PATTERN);

    // // Lấy phần mở rộng
    // String originalMediaFileName = media.getOriginalFilename();
    // if (originalMediaFileName == null || !originalMediaFileName.contains(".")) {
    // throw new RuntimeException("Invalid file name");
    // }

    // String extension = FileUploadUtils.getExtension(originalMediaFileName);
    // String resourceType =
    // FileUploadUtils.getResourceTypeFromExtension(extension);

    // String mediaCloudinary =
    // FileUploadUtils.generateFileName(originalMediaFileName);
    // // File tạm
    // File tempMediaFile = new File(System.getProperty("java.io.tmpdir"),
    // mediaCloudinary);
    // media.transferTo(tempMediaFile);

    // // Tạo public_id
    // String publicID = "/" + mediaCloudinary.substring(0,
    // mediaCloudinary.lastIndexOf('.'));
    // // Upload
    // Map uploadResult = cloudinary.uploader().upload(tempMediaFile,
    // ObjectUtils.asMap(
    // "folder", folderName,
    // "public_id", publicID,
    // "overwrite", true,
    // "resource_type", resourceType));

    // return uploadResult;
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to upload file to Cloudinary",
    // e.fillInStackTrace());
    // } finally {
    // // Xoá file tạm
    // File tempFile = new File(System.getProperty("java.io.tmpdir"),
    // media.getOriginalFilename());
    // if (tempFile.exists()) {
    // tempFile.delete();
    // }
    // }

    // }

    //
    // ĐÂY LÀ CODE ĐANG ÁP DỤNG
    @Override
    public Map<String, Object> uploadMedia(MultipartFile media, String folderName, int width,
            int height) {
        try {
            // 1. Kiểm tra tính hợp lệ (Size, Extension) bằng Utils của bạn
            FileUploadUtils.assertAllowed(media, FileUploadUtils.MEDIA_PATTERN);

            // 2. Chuẩn bị tên file và public_id
            String originalFileName = media.getOriginalFilename();

            if (originalFileName == null) {
                throw new RuntimeException("Invalid file name");
            }

            String cleanFileName = originalFileName
                    .trim()
                    .replaceAll("\\s+", "_")
                    .replaceAll("[()\\\\/:*?\"<>|]", "");

            // Đảm bảo tên file sau khi dọn dẹp vẫn còn giá trị
            if (cleanFileName.isEmpty()) {
                cleanFileName = "default_name_" + System.currentTimeMillis();
            }
            String fileNameGenerated = FileUploadUtils.generateFileName(cleanFileName);

            // Cloudinary public_id không nên bao gồm đuôi file
            String publicId = fileNameGenerated.contains(".")
                    ? fileNameGenerated.substring(0, fileNameGenerated.lastIndexOf('.'))
                    : fileNameGenerated;

            // 3. Cấu hình Transformation để Resize & Optimize
            // f_auto: tự chuyển sang WebP/Avif, q_auto: nén dung lượng mà không giảm
            // chất
            // lượng mắt nhìn
            Transformation transformation = new Transformation()
                    .width(width) // Bạn có thể chỉnh lại width tùy ý
                    .height(height) // Bạn có thể chỉnh lại height tùy ý
                    .crop("limit") // "limit": chỉ thu nhỏ nếu ảnh lớn hơn 1920, không phóng to ảnh nhỏ
                    .quality("auto") // Tự động nén tối ưu
                    .fetchFormat("auto"); // Tự động chuyển định dạng WebP/Avif

            // 4. Thiết lập tham số upload
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folderName,
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "auto" // Tự động nhận diện ảnh hay video
            );

            // Chỉ áp dụng transformation nếu là ảnh (Tránh lỗi khi upload video)
            String extension = FileUploadUtils.getExtension(originalFileName);
            if (!FileUploadUtils.getResourceTypeFromExtension(extension).equals("video")) {
                params.put("transformation", transformation);
            }

            // 5. Upload trực tiếp từ mảng byte (RAM) -> Nhanh và không để lại file rác
            return cloudinary.uploader().upload(media.getBytes(), params);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc dữ liệu file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi upload Cloudinary: " + e.getMessage());
        }
    }

    // @Override
    // public Map<String, Object> uploadMedia(File tempFile, String folderName, int
    // width,
    // int height) {
    // try {
    // // Lấy tên file từ file tạm
    // String fileName = tempFile.getName();

    // String publicId = fileName.contains(".") ? fileName.substring(0,
    // fileName.lastIndexOf('.')) : fileName;
    // Transformation transformation = new Transformation()
    // .width(width) // Bạn có thể chỉnh lại width tùy ý
    // .height(height) // Bạn có thể chỉnh lại height tùy ý
    // .crop("limit") // "limit": chỉ thu nhỏ nếu ảnh lớn hơn 1920, không phóng to
    // ảnh nhỏ
    // .quality("auto") // Tự động nén tối ưu
    // .fetchFormat("auto"); // Tự động chuyển định dạng WebP/Avif

    // // 4. Thiết lập tham số upload
    // Map<String, Object> params = ObjectUtils.asMap(
    // "folder", folderName,
    // "public_id", publicId,
    // "overwrite", true,
    // "resource_type", "auto" // Tự động nhận diện ảnh hay video
    // );

    // // Chỉ áp dụng transformation nếu là ảnh (Tránh lỗi khi upload video)
    // String extension = FileUploadUtils.getExtension(fileName);
    // if (!FileUploadUtils.getResourceTypeFromExtension(extension).equals("video"))
    // {
    // params.put("transformation", transformation);
    // }

    // // 5. Upload trực tiếp từ mảng byte (RAM) -> Nhanh và không để lại file rác
    // return cloudinary.uploader().upload(tempFile, params);

    // } catch (Exception e) {
    // throw new RuntimeException("Lỗi upload Cloudinary: " + e.getMessage());
    // }
    // }

    public void deleteMedia(String publicId) {
        try {
            if (publicId == null || publicId.isEmpty()) {
                throw new IllegalArgumentException("Public ID cannot be null or empty");
            }

            // Assume resource type is image
            String resourceType = "image";

            // Delete from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "invalidate", true));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }

    }

    // @Override
    // public File saveToTemp(MultipartFile media) {
    // try {

    // // 1. Kiểm tra tính hợp lệ (Size, Extension) bằng Utils của bạn
    // FileUploadUtils.assertAllowed(media, FileUploadUtils.MEDIA_PATTERN);

    // // 2. Chuẩn bị tên file và public_id
    // String originalFileName = media.getOriginalFilename();

    // if (originalFileName == null) {
    // throw new RuntimeException("Invalid file name");
    // }

    // String cleanFileName = originalFileName
    // .trim()
    // .replaceAll("\\s+", "_")
    // .replaceAll("[()\\\\/:*?\"<>|]", "");
    // // .replaceAll("[^a-zA-Z0-9._-]", "");

    // // Đảm bảo tên file sau khi dọn dẹp vẫn còn giá trị
    // if (cleanFileName.isEmpty()) {
    // cleanFileName = "temp_" + System.currentTimeMillis();
    // }

    // String fileNameGenerated = FileUploadUtils.generateFileName(cleanFileName);
    // File tempFile = File.createTempFile(System.getProperty("java.io.tmpdir"), "_"
    // + fileNameGenerated);

    // media.transferTo(tempFile);

    // return tempFile;
    // } catch (Exception e) {
    // throw new RuntimeException("Lỗi tạo file tạm: " + e.getMessage());
    // }
    // }

}
