package com.CRM.service.Brand;

import java.util.Date;
import java.util.List;
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
import com.CRM.model.Role;
import com.CRM.repository.IBrandRepository;
import com.CRM.repository.ICategoryRepository;
import com.CRM.repository.IMediaRepository;
import com.CRM.repository.Specification.BrandSpecification;
import com.CRM.repository.Specification.MediaSpecification;
import com.CRM.request.Brand.brandRequest;
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
    public PagingResponse<BrandResponse> getAllBrand(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BrandSpecification.getAllBrand(),
                BrandResponse.class,
                iBrandRepository);
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createBrand(brandRequest brandRequest, MultipartFile image, int width,
            int height) {

        // ServletRequestAttributes attributes = (ServletRequestAttributes)
        // RequestContextHolder.getRequestAttributes();
        // if (attributes != null && attributes.getRequest() instanceof
        // MultipartHttpServletRequest) {
        // MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
        // attributes.getRequest();
        // if (multipartRequest.getFileMap().size() > 1) {
        // throw new IllegalArgumentException("Only one image can be uploaded.");
        // }
        // }
        if (iBrandRepository.existsActiveByName(brandRequest.getName())) {
            throw new IllegalArgumentException("Brand name already exists and is active");
        }

        Category category = iCategoryRepository.findById(brandRequest.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Brand brand = modelMapper.map(brandRequest, Brand.class);
        brand.setCategory(category);
        brand.setInActive(false);
        brand.setCreatedDate(new Date());
        brand.setModifiedDate(new Date());
        brand.setCode(randomCode());
        brand.setDeletedAt(0L);
        brand.setDeleted(false);

        brand = iBrandRepository.save(brand);
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Brand image is required");
        }

        try {
            Map uploadResult = cloudinaryService.uploadMedia(image, "crm/brands", width, height);
            String uploadedPublicId = (String) uploadResult.get("public_id");
            String mediaUrl = (String) uploadResult.get("secure_url");

            Media brandMedia = Media.builder()
                    .imageUrl(mediaUrl)
                    .publicId(uploadedPublicId)
                    .referenceId(brand.getId())
                    .referenceType("BRAND")
                    .altText(brand.getName())
                    .type("MEDIA")
                    .build();
            brandMedia.setInActive(false);
            brandMedia.setCreatedDate(new Date());
            brandMedia.setModifiedDate(new Date());
            brandMedia.setCode(randomCode());
            brandMedia.setDeletedAt(0L);
            brandMedia.setDeleted(false);
            brand.setImage(brandMedia);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image, please try again.");
        }
        iBrandRepository.save(brand);
        return new APIResponse<>(true, List.of("Brand created successfully"));
    }

    @Override
    @Transactional
    public APIResponse<Boolean> updateBrand(String id, brandRequest brandRequest, MultipartFile image, int width,
            int height) {
        Brand brand = iBrandRepository.findById(UUID.fromString(id)).orElse(null);
        if (brand == null) {
            throw new IllegalArgumentException("Brand not found !");
        }
        Category category = iCategoryRepository.findById(brandRequest.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found !"));
        modelMapper.map(brandRequest, brand);
        brand.setCategory(category);
        brand.setModifiedDate(new Date());

        if (image != null && !image.isEmpty()) {
            try {
                if (brand.getImage() != null) {
                    cloudinaryService.deleteMedia(brand.getImage().getPublicId());
                    brand.setImage(new Media());
                }
                Map uploadResult = cloudinaryService.uploadMedia(image, "crm/brands", width, height);
                String uploadedPublicId = (String) uploadResult.get("public_id");
                String mediaUrl = (String) uploadResult.get("secure_url");
                Media brandMedia = Media.builder()
                        .imageUrl(mediaUrl)
                        .publicId(uploadedPublicId)
                        .referenceId(brand.getId())
                        .referenceType("BRAND")
                        .altText(brand.getName())
                        .type("IMAGE")
                        .build();
                brandMedia.setCreatedDate(new Date());
                brandMedia.setModifiedDate(new Date());
                brand.setImage(brandMedia);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image, please try again.");
            }
        }
        iBrandRepository.save(brand);
        return new APIResponse<>(true, List.of("Brand updated successfully"));

    }

    @Override
    public APIResponse<Boolean> deleteBrand(String id) {
        Brand brand = iBrandRepository.findById(UUID.fromString(id)).orElseThrow(
                () -> new IllegalArgumentException("Role not found"));
        Media brandMedia = brand.getImage();
        if (brandMedia != null && brandMedia.getPublicId() != null) {
            brandMedia.setInActive(true);
            brandMedia.setDeleted(true);
            brandMedia.setDeletedAt(System.currentTimeMillis() / 1000);
        }
        brand.setInActive(true);
        brand.setDeleted(true);
        brand.setDeletedAt(System.currentTimeMillis() / 1000);
        iBrandRepository.save(brand);
        return new APIResponse<>(true, List.of("Brand deleted successfully and moved to Recybin"));
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
                return new APIResponse<>(false, List.of("Restore operation was cancelled."));
            }

            // Nếu user chọn Ghi đè (OVERWRITE)
            if (action == RestoreEnum.OVERWRITE) {
                // Xóa role đang active trước
                iBrandRepository.delete(activeDuplicate.get());
                iBrandRepository.flush(); // Xóa ngay để tránh trùng Unique Key khi save bên dưới
            }
        }
        brandInTrash.setInActive(false);
        brandInTrash.setDeleted(false);
        brandInTrash.setDeletedAt(0L);

        // roleInTrash.setCode(null);

        iBrandRepository.save(brandInTrash);
        return new APIResponse<>(true, List.of("Restored successfully."));
    }

}
