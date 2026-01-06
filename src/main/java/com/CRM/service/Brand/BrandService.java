package com.CRM.service.Brand;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Brand;
import com.CRM.model.Category;
import com.CRM.model.Media;
import com.CRM.repository.IBrandRepository;
import com.CRM.repository.ICategoryRepository;
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
                null,
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
        if (iBrandRepository.existsByName(brandRequest.getName())) {
            throw new IllegalArgumentException("Brand already exists");
        }

        Category category = iCategoryRepository.findById(brandRequest.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Brand brand = modelMapper.map(brandRequest, Brand.class);
        brand.setCategory(category);
        brand.setCreatedDate(new Date());
        brand.setModifiedDate(new Date());

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
            brandMedia.setCreatedDate(new Date());
            brandMedia.setModifiedDate(new Date());
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
    public APIResponse<Boolean> updateBrand(UUID id, brandRequest brandRequest, MultipartFile image, int width,
            int height) {
        Brand brand = iBrandRepository.findById(id).orElse(null);
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
    public APIResponse<Boolean> deleteBrand(UUID id) {
        Brand brand = iBrandRepository.findById(id).orElse(null);
        if (brand == null) {
            throw new IllegalArgumentException("Brand not found !");
        }

        Media brandMedia = brand.getImage();
        if (brandMedia != null && brandMedia.getPublicId() != null) {
            cloudinaryService.deleteMedia(brandMedia.getPublicId());
        }

        iBrandRepository.delete(brand);

        return new APIResponse<>(true, List.of());
    }

}
