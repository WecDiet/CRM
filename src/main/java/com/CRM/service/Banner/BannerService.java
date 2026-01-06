package com.CRM.service.Banner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.CRM.Util.Helper.HelperService;
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
                null,
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
        banner.setCreatedDate(new Date());
        banner.setModifiedDate(new Date());

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
            bannerMedia.setCreatedDate(new Date());
            bannerMedia.setModifiedDate(new Date());
            banner.setImage(bannerMedia);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image, please try again.");
        }
        iBannerRepository.save(banner);
        return new APIResponse<>(true, List.of("Banner created successfully"));
    }

    @Override
    public APIResponse<Boolean> updateBanner(UUID id, bannerRequest bannerRequest, MultipartFile media, int width,
            int height) throws NotFoundException {
        Banner banner = iBannerRepository.findById(id)
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
    public APIResponse<Boolean> deleteBanner(UUID id) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteBanner'");
    }

    @Override
    public APIResponse<Boolean> uploadMediaBanner(String bannerID, MultipartFile media) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadMediaBanner'");
    }
}