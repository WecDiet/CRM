package com.CRM.service.Banner;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.model.Banner;
import com.CRM.model.Brand;
import com.CRM.model.Media;
import com.CRM.repository.IBannerRepository;
import com.CRM.repository.IBrandRepository;
import com.CRM.request.Banner.bannerRequest;
import com.CRM.response.Banner.BannerResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.service.Cloudinary.CloudinaryService;
import com.CRM.service.Helper.HelperService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BannerService extends HelperService<Banner, Long> implements IBannerService {

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
                null,
                BannerResponse.class,
                iBannerRepository);
    }

    @Transactional
    @Override
    public APIResponse<Boolean> createBanner(bannerRequest bannerRequest, MultipartFile media, int width, int height)
            throws NotFoundException {
        if (iBannerRepository.existsByTitle(bannerRequest.getTitle())) {
            throw new IllegalArgumentException("Banner title already exists");
        }

        Brand brand = iBrandRepository.findById(bannerRequest.getBrandID())
                .orElseThrow(() -> new NotFoundException());
        Banner banner = modelMapper.map(bannerRequest, Banner.class);
        banner.setBrand(brand);
        // banner = iBannerRepository.save(banner);

        // 3. Xử lý Upload Media
        if (media == null || media.isEmpty()) {
            throw new IllegalArgumentException("Banner image is required");
        }

        String uploadedPublicId = null;
        try {
            Map uploadResult = cloudinaryService.uploadMedia(media, "banners", width, height);
            uploadedPublicId = (String) uploadResult.get("public_id");
            String mediaUrl = (String) uploadResult.get("secure_url");

            Media bannerMedia = Media.builder()
                    .imageUrl(mediaUrl)
                    .publicId(uploadedPublicId)
                    .referenceId(banner.getId())
                    .referenceType("BANNER")
                    .altText(banner.getTitle())
                    .type("MEDIA")
                    .build();
            banner.setImages(List.of(bannerMedia));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image, please try again.");
        }

        // // 4. Lưu vào Database (QUAN TRỌNG: Hibernate sẽ cascade lưu cả Banner và
        // Media
        // // nếu cấu hình đúng)
        // Banner savedBanner = iBannerRepository.save(banner);

        // // Cập nhật lại referenceId cho Media nếu cần (nếu DB tự sinh ID cho Banner)
        // if (savedBanner.getImages() != null) {
        // savedBanner.getImages().forEach(bannerMedia ->
        // bannerMedia.setReferenceId(savedBanner.getId()));
        // // iMediaRepository.save(savedBanner.getImage()); // Nếu cascade không tự
        // update
        // iBannerRepository.save(savedBanner); // Save lại lần nữa để cập nhật
        // referenceId
        // }

        try {
            iBannerRepository.save(banner);
        } catch (Exception e) {
            // ROLLBACK CLOUDINARY: Nếu lưu DB lỗi, phải xóa ảnh vừa up lên để tránh rác
            if (banner.getImages() != null) {
                banner.getImages().forEach(mediaItem -> {
                    cloudinaryService.deleteMedia(mediaItem.getPublicId());
                });
            }
            throw new RuntimeException("Database error: Could not save banner");
        }
        return new APIResponse<>(true, List.of("Banner created successfully"));
    }

    @Override
    public APIResponse<Boolean> updateBanner(Long id, bannerRequest bannerRequest) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBanner'");
    }

    @Override
    public APIResponse<Boolean> deleteBanner(Long id) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBanner'");
    }

    @Override
    public APIResponse<Boolean> uploadMediaBanner(String bannerID, MultipartFile media) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadMediaBanner'");
    }
}