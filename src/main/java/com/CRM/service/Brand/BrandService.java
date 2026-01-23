package com.CRM.service.Brand;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.enums.RestoreEnum;
import com.CRM.model.Brand;
import com.CRM.model.Category;
import com.CRM.model.Media;
import com.CRM.repository.IBrandRepository;
import com.CRM.repository.ICategoryRepository;
import com.CRM.repository.Specification.BrandSpecification;
import com.CRM.request.Brand.BrandRequest;
import com.CRM.response.Brand.BrandResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService extends HelperService<Brand, UUID> implements IBrandService {

    @Autowired
    private IBrandRepository iBrandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ICategoryRepository iCategoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public PagingResponse<BrandResponse> getAllBrand(int page, int limit, String sortBy, String direction,
            String categotyName, boolean active) {

        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BrandSpecification.getAllBrandByCategory(categotyName, active),
                BrandResponse.class,
                iBrandRepository);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // Đảm bảo rollback khi có bất kỳ exception nào
    public APIResponse<Boolean> createBrand(BrandRequest brandRequest, MultipartFile image, int width, int height) {
        // 1. Kiểm tra ảnh trước khi làm bất cứ việc gì để tránh lãng phí tài nguyên
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Brand image is required");
        }

        Category category = iCategoryRepository.findById(UUID.fromString(brandRequest.getCategory()))
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        // 2. Kiểm tra nghiệp vụ (Tên tồn tại, Category tồn tại)
        if (iBrandRepository.existsActiveByName(brandRequest.getName())) {
            throw new IllegalArgumentException("Brand name already exists and is active");
        }

        try {
            // 3. Upload lên Cloudinary trước
            Map uploadResult = cloudinaryService.uploadMedia(image, "crm/brands", width,
                    height);
            String uploadedPublicId = (String) uploadResult.get("public_id");
            String mediaUrl = (String) uploadResult.get("secure_url");

            // 4. Map dữ liệu vào Entity
            Brand brand = modelMapper.map(brandRequest, Brand.class);
            brand.setCategory(category);
            brand.setInActive(brandRequest.isActive());
            brand.setCreatedDate(new Date());
            brand.setModifiedDate(new Date());
            brand.setCode(randomCode());
            brand.setDeletedAt(0L);
            brand.setDeleted(false);

            // Tạo Media entity
            Media brandMedia = Media.builder()
                    .imageUrl(mediaUrl)
                    .publicId(uploadedPublicId)
                    .referenceId(brand.getId())
                    .referenceType("BRAND")
                    .altText(brand.getName())
                    .type("IMAGE")
                    .build();
            // Set metadata cho Media (nên dùng BaseEntity listener để tự động phần này)
            brandMedia.setInActive(brandRequest.isActive());
            brandMedia.setCreatedDate(new Date());
            brandMedia.setModifiedDate(new Date());
            brandMedia.setCode(randomCode());
            brandMedia.setDeletedAt(0L);
            brandMedia.setDeleted(false);

            // Gán media vào brand
            brand.setImage(brandMedia);

            // 5. Lưu vào database (Chỉ lưu 1 lần duy nhất nhờ CascadeType.ALL)
            iBrandRepository.save(brand);

            return new APIResponse<>(true, "Brand created successfully");

        } catch (Exception e) {
            // 6. NẾU LỖI: Xóa ảnh trên Cloudinary để tránh rác dữ liệu
            Media media = new Media();
            String uploadedPublicId = media.getPublicId();
            if (uploadedPublicId != null) {
                try {
                    cloudinaryService.deleteMedia(uploadedPublicId); // Giả sử bạn có hàm này
                } catch (Exception deleteEx) {
                    // Log lỗi xóa ảnh nhưng vẫn throw lỗi chính để rollback DB
                    System.err.println("Failed to cleanup Cloudinary image: " +
                            uploadedPublicId);
                }
            }

            // Throw lỗi để @Transactional thực hiện rollback Database
            throw new RuntimeException("Failed to create brand: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public APIResponse<Boolean> updateBrand(String id, BrandRequest brandRequest, MultipartFile image, int width,
            int height) {
        Brand brand = iBrandRepository.findById(UUID.fromString(id)).orElse(null);
        if (brand == null) {
            throw new IllegalArgumentException("Brand not found !");
        }
        Category category = iCategoryRepository.findById(UUID.fromString(brandRequest.getCategory()))
                .orElseThrow(() -> new IllegalArgumentException("Category not found !"));

        if (image != null && !image.isEmpty()) {
            try {
                if (brand.getImage() != null) {
                    cloudinaryService.deleteMedia(brand.getImage().getPublicId());
                    brand.setImage(new Media());
                }
                // 3. Upload lên Cloudinary trước
                Map uploadResult = cloudinaryService.uploadMedia(image, "crm/brands", width,
                        height);
                String uploadedPublicId = (String) uploadResult.get("public_id");
                String mediaUrl = (String) uploadResult.get("secure_url");

                // 4. Map dữ liệu vào Entity
                modelMapper.map(brandRequest, brand);
                brand.setCategory(category);
                brand.setModifiedDate(new Date());
                brand.setInActive(brandRequest.isActive());

                // Tạo Media entity
                Media brandMedia = Media.builder()
                        .imageUrl(mediaUrl)
                        .publicId(uploadedPublicId)
                        .referenceType("BRAND")
                        .altText(brand.getName())
                        .type("MEDIA")
                        .build();
                // Set metadata cho Media (nên dùng BaseEntity listener để tự động phần này)
                brandMedia.setInActive(false);
                brandMedia.setCreatedDate(new Date());
                brandMedia.setModifiedDate(new Date());
                brandMedia.setCode(randomCode());
                brandMedia.setDeletedAt(0L);
                brandMedia.setDeleted(false);

                // Gán media vào brand
                brand.setImage(brandMedia);

                // 5. Lưu vào database (Chỉ lưu 1 lần duy nhất nhờ CascadeType.ALL)
                iBrandRepository.save(brand);

                return new APIResponse<>(true, "Brand updated successfully");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image, please try again.");
            }
        }
        iBrandRepository.save(brand);
        return new APIResponse<>(true, "Brand updated successfully");
    }

    @Override
    public APIResponse<Boolean> deleteBrand(String id) {
        Brand brand = iBrandRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new IllegalArgumentException("Role not found"));
        Media brandMedia = brand.getImage();
        if (brandMedia != null && brandMedia.getPublicId() != null) {
            brandMedia.setInActive(false);
            brandMedia.setDeleted(true);
            brandMedia.setDeletedAt(System.currentTimeMillis() / 1000);
        }
        brand.setInActive(false);
        brand.setDeleted(true);
        brand.setDeletedAt(System.currentTimeMillis() / 1000);
        iBrandRepository.save(brand);
        return new APIResponse<>(true, "Brand deleted successfully and moved to Recybin");
    }

    @Override
    public PagingResponse<BrandResponse> getAllBrandTrash(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BrandSpecification.getAllBrandTrash(),
                BrandResponse.class,
                iBrandRepository);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanBrandTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60; // 2 phút xóa
        int warningMinutes = 1; // 1 phút cảnh báo

        long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;

        cleanTrash(iBrandRepository,
                BrandSpecification.warningThreshold(warningThreshold), // Truyền thời gian thông báo trước khi xóa
                BrandSpecification.deleteThreshold(deleteThreshold), // Truyền thời gian sẽ bị xóa cứng
                warningMinutes,
                "BRAND",
                (brand) -> {
                    String publicId = brand.getImage().getPublicId();
                    if (publicId != null && !publicId.isEmpty()) {
                        cloudinaryService.deleteMedia(publicId);
                    }
                });
    }

    @Override
    public APIResponse<Boolean> restoreBrand(String id, RestoreEnum action) {
        // Tìm bản ghi trong thùng rác
        Brand brandInTrash = iBrandRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("This brand doesn't belong in the trash can."));

        // Tìm bản ghi trùng đang hoạt động
        Optional<Brand> activeDuplicate = iBrandRepository.findActiveByName(brandInTrash.getName());

        if (activeDuplicate.isPresent()) {
            // Nếu user chưa xác nhận hành động (lần gọi đầu tiên)
            if (action == null || action == RestoreEnum.RESTORE) {
                throw new IllegalArgumentException("CONFLICT: A brand with this name already exists.");
            }

            // Nếu user chọn Bỏ qua
            if (action == RestoreEnum.CANCEL) {
                return new APIResponse<>(false, "Restore operation was cancelled.");
            }

            // Nếu user chọn Ghi đè (OVERWRITE)
            if (action == RestoreEnum.OVERWRITE) {
                // Xóa role đang active trước
                iBrandRepository.delete(activeDuplicate.get());
                iBrandRepository.flush(); // Xóa ngay để tránh trùng Unique Key khi save bên dưới
            }
        }
        brandInTrash.setInActive(true);
        brandInTrash.setDeleted(false);
        brandInTrash.setDeletedAt(0L);

        // roleInTrash.setCode(null);

        iBrandRepository.save(brandInTrash);
        return new APIResponse<>(true, "Restored successfully.");
    }

}
