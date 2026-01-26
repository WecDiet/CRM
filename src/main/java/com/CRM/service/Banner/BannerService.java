package com.CRM.service.Banner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.enums.RestoreEnum;
import com.CRM.model.Banner;
import com.CRM.model.Brand;
import com.CRM.model.Media;
import com.CRM.repository.IBannerRepository;
import com.CRM.repository.IBrandRepository;
import com.CRM.repository.IMediaRepository;
import com.CRM.repository.Specification.BannerSpecification;
import com.CRM.repository.Specification.BrandSpecification;
import com.CRM.request.Banner.BannerRequest;
import com.CRM.response.Banner.BannerResponse;
import com.CRM.response.Brand.BrandResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannerService extends HelperService<Banner, UUID> implements IBannerService {

    @Autowired
    private IBannerRepository iBannerRepository;

    @Autowired
    private IBrandRepository iBrandRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IMediaRepository iMediaRepository;

    @Override
    public PagingResponse<BannerResponse> getAllBanners(int page, int limit, String sortBy, String direction,
            boolean active) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BannerSpecification.getAllBanner(active),
                BannerResponse.class,
                iBannerRepository);
    }

    @Transactional
    @Override
    public APIResponse<Boolean> createBanner(BannerRequest bannerRequest, MultipartFile media, int width, int height) {
        if (iBannerRepository.existsActiveByName(bannerRequest.getName())) {
            throw new IllegalArgumentException("Banner name already exists and is active");
        }

        Brand brand = iBrandRepository.findById(UUID.fromString(bannerRequest.getBrand()))
                .orElseThrow(() -> new IllegalArgumentException("Brand not found !"));

        if (!"collection".equalsIgnoreCase(brand.getCategory().getName().trim())) {
            throw new IllegalArgumentException(
                    "Banner creation is only permitted for Brands in the 'Collection' category.");
        }
        // 3. Xử lý Upload Media
        if (media == null || media.isEmpty()) {
            throw new IllegalArgumentException("Banner image is required");
        }

        String uploadedPublicId = null;
        try {
            CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService
                    .uploadMedia(media, "crm/banner", width, height);

            Map<String, Object> uploadResult = uploadFuture.join();
            uploadedPublicId = (String) uploadResult.get("public_id");
            String mediaUrl = (String) uploadResult.get("secure_url");

            Banner banner = modelMapper.map(bannerRequest, Banner.class);
            banner.setBrand(brand);
            banner.setInActive(bannerRequest.isActive());
            banner.setCreatedDate(new Date());
            banner.setModifiedDate(new Date());
            banner.setCode(randomCode());
            banner.setDeletedAt(0L);
            banner.setDeleted(false);

            Media bannerMedia = Media.builder()
                    .imageUrl(mediaUrl)
                    .publicId(uploadedPublicId)
                    .referenceId(banner.getId())
                    .referenceType("BANNER")
                    .altText(banner.getName())
                    .type("MEDIA")
                    .build();
            banner.setImage(bannerMedia);
            iBannerRepository.save(banner);
            return new APIResponse<>(true, "Banner created successfully");

        } catch (Exception e) {
            // 6. NẾU LỖI: Xóa ảnh trên Cloudinary để tránh rác dữ liệu
            // Media mediaException = new Media();
            // uploadedPublicId = mediaException.getPublicId();
            if (uploadedPublicId != null) {
                try {
                    cloudinaryService.deleteMedia(uploadedPublicId).join(); // Giả sử bạn có hàm này
                } catch (Exception deleteEx) {
                    // Log lỗi xóa ảnh nhưng vẫn throw lỗi chính để rollback DB
                    System.err.println("Failed to cleanup Cloudinary image: " +
                            uploadedPublicId);
                }
            }

            // Throw lỗi để @Transactional thực hiện rollback Database
            throw new RuntimeException("Failed to create banner: " + e.getMessage());
        }
    }

    @Override
    public APIResponse<Boolean> updateBanner(String id, BannerRequest bannerRequest, MultipartFile media, int width,
            int height) {
        Banner banner = iBannerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Banner not found. "));
        Brand brand = iBrandRepository.findById(UUID.fromString(bannerRequest.getBrand()))
                .orElseThrow(() -> new IllegalArgumentException("Brand not found. "));

        modelMapper.map(bannerRequest, banner);
        banner.setBrand(brand);
        banner.setModifiedDate(new Date());

        if (media != null && !media.isEmpty()) {
            try {
                if (banner.getImage() != null) {
                    cloudinaryService.deleteMedia(banner.getImage().getPublicId());
                    banner.setImage(new Media());
                }

                // Map uploadResult = cloudinaryService.uploadMedia(media, "crm/banner", width,
                // height);

                // String uploadedPublicId = (String) uploadResult.get("public_id");
                // String mediaUrl = (String) uploadResult.get("secure_url");
                // Media bannerMedia = Media.builder()
                // .imageUrl(mediaUrl)
                // .publicId(uploadedPublicId)
                // .referenceId(banner.getId())
                // .referenceType("BANNER")
                // .altText(banner.getName())
                // .type("MEDIA")
                // .build();
                // bannerMedia.setModifiedDate(new Date());
                // banner.setImage(bannerMedia);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image, please try again.");
            }
        }
        iBannerRepository.save(banner);
        return new APIResponse<>(true, "Banner updated successfully");
    }

    @Override
    public APIResponse<Boolean> deleteBanner(String id) {
        Banner banner = iBannerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Banner not found !"));

        Media bannerMedia = banner.getImage();
        if (bannerMedia != null && bannerMedia.getPublicId() != null) {
            bannerMedia.setInActive(false);
            bannerMedia.setDeleted(true);
            bannerMedia.setDeletedAt(System.currentTimeMillis() / 1000);
            iMediaRepository.save(bannerMedia);
        }

        banner.setInActive(false);
        banner.setDeleted(true);
        banner.setDeletedAt(System.currentTimeMillis() / 1000);

        iBannerRepository.save(banner);
        return new APIResponse<>(true, "Banner deleted successfully and move to Recybin");
    }

    @Override
    public PagingResponse<BannerResponse> getAllBannerTrash(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BannerSpecification.getAllBannerTrash(), BannerResponse.class,
                iBannerRepository);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanBannerTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60; // 2 phút xóa
        int warningMinutes = 1; // 1 phút cảnh báo

        long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;

        cleanTrash(iBannerRepository,
                BannerSpecification.warningThreshold(warningThreshold), // Truyền thời gian thông báo trước khi xóa
                BannerSpecification.deleteThreshold(deleteThreshold), // Truyền thời gian sẽ bị xóa cứng
                warningMinutes,
                "BANNER",
                (banner) -> {
                    String publicId = banner.getImage().getPublicId();
                    if (publicId != null && !publicId.isEmpty()) {
                        cloudinaryService.deleteMedia(publicId);
                    }
                });
    }

    @Override
    public APIResponse<Boolean> restoreBanner(String id, RestoreEnum action) {
        // Tìm bản ghi trong thùng rác
        Banner bannerInTrash = iBannerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("This banner doesn't belong in the trash can."));

        // Tìm bản ghi trùng đang hoạt động
        Optional<Banner> activeDuplicate = iBannerRepository.findActiveByName(bannerInTrash.getName());

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
                iBannerRepository.delete(activeDuplicate.get());
                iBannerRepository.flush(); // Xóa ngay để tránh trùng Unique Key khi save bên dưới
            }
        }
        bannerInTrash.setInActive(true);
        bannerInTrash.setDeleted(false);
        bannerInTrash.setDeletedAt(0L);

        // roleInTrash.setCode(null);

        iBannerRepository.save(bannerInTrash);
        return new APIResponse<>(true, "Restored successfully.");
    }

}