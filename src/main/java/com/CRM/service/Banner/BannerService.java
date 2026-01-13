package com.CRM.service.Banner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import com.CRM.repository.Specification.BannerSpecification;
import com.CRM.repository.Specification.BrandSpecification;
import com.CRM.request.Banner.bannerRequest;
import com.CRM.response.Banner.BannerResponse;
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

    @Override
    public PagingResponse<BannerResponse> getAllBanners(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BannerSpecification.getAllBanner(),
                BannerResponse.class,
                iBannerRepository);
    }

    @Transactional
    @Override
    public APIResponse<Boolean> createBanner(bannerRequest bannerRequest, MultipartFile media, int width, int height)
            throws NotFoundException {

        // ServletRequestAttributes attributes = (ServletRequestAttributes)
        // RequestContextHolder.getRequestAttributes();
        // if (attributes != null && attributes.getRequest() instanceof
        // MultipartHttpServletRequest) {
        // MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
        // attributes.getRequest();
        // if (multipartRequest.getFileMap().size() > 4) {
        // throw new IllegalArgumentException("Max four image can be uploaded.");
        // }
        // }
        if (iBannerRepository.existsByTitle(bannerRequest.getTitle())) {
            throw new IllegalArgumentException("Banner title already exists");
        }

        Brand brand = iBrandRepository.findById(bannerRequest.getBrandID())
                .orElseThrow(() -> new NotFoundException());
        Banner banner = modelMapper.map(bannerRequest, Banner.class);
        banner.setBrand(brand);
        banner.setInActive(false);
        banner.setCreatedDate(new Date());
        banner.setModifiedDate(new Date());
        banner.setCode(randomCode());
        banner.setDeletedAt(0L);
        banner.setDeleted(false);

        banner = iBannerRepository.save(banner);

        // 3. Xử lý Upload Media
        if (media == null || media.isEmpty()) {
            throw new IllegalArgumentException("Banner image is required");
        }

        try {
            Map uploadResult = cloudinaryService.uploadMedia(media, "crm/banners", width, height);
            String uploadedPublicId = (String) uploadResult.get("public_id");
            String mediaUrl = (String) uploadResult.get("secure_url");

            Media bannerMedia = Media.builder()
                    .imageUrl(mediaUrl)
                    .publicId(uploadedPublicId)
                    .referenceId(banner.getId())
                    .referenceType("BANNER")
                    .altText(banner.getTitle())
                    .type("MEDIA")
                    .build();
            bannerMedia.setInActive(false);
            bannerMedia.setCreatedDate(new Date());
            bannerMedia.setModifiedDate(new Date());
            bannerMedia.setCode(randomCode());
            bannerMedia.setDeletedAt(0L);
            bannerMedia.setDeleted(false);
            banner.setImage(bannerMedia);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image, please try again.");
        }
        iBannerRepository.save(banner);
        return new APIResponse<>(true, List.of("Banner created successfully"));
    }

    @Override
    public APIResponse<Boolean> updateBanner(String id, bannerRequest bannerRequest, MultipartFile media, int width,
            int height) throws NotFoundException {
        Banner banner = iBannerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Banner not found. "));
        Brand brand = iBrandRepository.findById(bannerRequest.getBrandID())
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

                Map uploadResult = cloudinaryService.uploadMedia(media, "crm/banner", width, height);

                String uploadedPublicId = (String) uploadResult.get("public_id");
                String mediaUrl = (String) uploadResult.get("secure_url");
                Media bannerMedia = Media.builder()
                        .imageUrl(mediaUrl)
                        .publicId(uploadedPublicId)
                        .referenceId(banner.getId())
                        .referenceType("BANNER")
                        .altText(banner.getTitle())
                        .type("MEDIA")
                        .build();
                bannerMedia.setModifiedDate(new Date());
                banner.setImage(bannerMedia);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image, please try again.");
            }
        }
        iBannerRepository.save(banner);
        return new APIResponse<>(true, List.of("Banner updated successfully"));
    }

    @Override
    public APIResponse<Boolean> deleteBanner(String id) throws NotFoundException {
        Banner bannerDelete = iBannerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> (new IllegalArgumentException("Banner not found")));
        Media bannerMedia = bannerDelete.getImage();
        if (bannerMedia != null && bannerMedia.getPublicId() != null) {
            bannerMedia.setInActive(true);
            bannerMedia.setDeleted(true);
            bannerMedia.setDeletedAt(System.currentTimeMillis() / 1000);
        }

        bannerDelete.setInActive(true);
        bannerDelete.setDeleted(true);
        bannerDelete.setDeletedAt(System.currentTimeMillis() / 1000);
        iBannerRepository.save(bannerDelete);
        return new APIResponse<>(true, List.of("Banner deleted successfully and moved to Recybin"));
    }

    @Override
    public PagingResponse<BannerResponse> getAllBannerTrash(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                BannerSpecification.getAllBannerTrash(),
                BannerResponse.class,
                iBannerRepository);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanBannerTrash() {
        // TODO Auto-generated method stub
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
        Banner bannerInTrash = iBannerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("This brand doesn't belong in the trash can."));

        // Tìm bản ghi trùng đang hoạt động
        Optional<Banner> activeDuplicate = iBannerRepository.findActiveByTitle(bannerInTrash.getTitle());

        if (activeDuplicate.isPresent()) {
            // Nếu user chưa xác nhận hành động (lần gọi đầu tiên)
            if (action == null || action == RestoreEnum.RESTORE) {
                throw new IllegalArgumentException("CONFLICT: A Banner with this title already exists.");
            }

            // Nếu user chọn Bỏ qua
            if (action == RestoreEnum.CANCEL) {
                return new APIResponse<>(false, List.of("Restore operation was cancelled."));
            }

            // Nếu user chọn Ghi đè (OVERWRITE)
            if (action == RestoreEnum.OVERWRITE) {
                // Xóa role đang active trước
                iBannerRepository.delete(activeDuplicate.get());
                iBannerRepository.flush(); // Xóa ngay để tránh trùng Unique Key khi save bên dưới
            }

        }
        bannerInTrash.setInActive(false);
        bannerInTrash.setDeleted(false);
        bannerInTrash.setDeletedAt(0L);

        iBannerRepository.save(bannerInTrash);
        return new APIResponse<>(true, List.of("Restored successfully."));
    }

}