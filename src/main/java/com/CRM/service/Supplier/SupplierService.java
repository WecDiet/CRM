package com.CRM.service.Supplier;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Image;
import com.CRM.model.Supplier;
import com.CRM.repository.ISupplierRepository;
import com.CRM.repository.Specification.SupplierSpecification;
import com.CRM.request.Supplier.SupplierFilterRequest;
import com.CRM.request.Supplier.SupplierRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Supplier.SupplierResponse;
import com.CRM.service.Cloudinary.CloudinaryService;


import com.CRM.response.Supplier.SupplierDetailResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService extends HelperService<Supplier, UUID> implements ISupplierService {

    private final ISupplierRepository iSupplierRepository;

    private final ModelMapper modelMapper;

    private final CloudinaryService cloudinaryService;

    @Override
    public PagingResponse<SupplierResponse> getAllSupplier(int page, int limit, String sortBy, String direction, boolean active,
            SupplierFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    SupplierSpecification.getAllSupplierFilter(filter, active), 
                    SupplierResponse.class, 
                    iSupplierRepository);
    }

    @Override
    public APIResponse<SupplierDetailResponse> getSupplierDetail(String id) {
        return getById(
                    UUID.fromString(id), 
                    iSupplierRepository, 
                    Supplier.class, 
                    SupplierDetailResponse.class
                );
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createSupplier(SupplierRequest supplierRequest, MultipartFile image, boolean active) throws BadRequestException {
        // 1. Validate bắt buộc
        if (supplierRequest.getName() == null || supplierRequest.getName().isEmpty()) {
            throw new BadRequestException("Supplier name is required");
        }

        if (supplierRequest.getStartDay() != null && supplierRequest.getStartMonth() != null && supplierRequest.getStartYear() != null) {
            if (!isValidDate(supplierRequest.getStartYear(), supplierRequest.getStartMonth(), supplierRequest.getStartDay())) {
                throw new BadRequestException("Invalid start date");
            }    
        }

        // 3. Check trùng taxCode
        if (supplierRequest.getTaxCode() != null && iSupplierRepository.existsByTaxCode(supplierRequest.getTaxCode())) {
            throw new BadRequestException("Tax code already exists");
        }

        String uploadedPublicId = null;
        try {            
            LocalDate colabDate = null;

            Integer day = supplierRequest.getStartDay();
            Integer month = supplierRequest.getStartMonth();
            Integer year = supplierRequest.getStartYear();

            if (day != null && month != null && year != null) {
                colabDate = LocalDate.of(year, month, day);
            }
            Supplier supplier = modelMapper.map(supplierRequest, Supplier.class);
            supplier.setSupplierCode("NCC_" + supplierRequest.getName().replace(" ", "_").toUpperCase());
            supplier.setInActive(active);
            supplier.setCreatedDate(new Date());
            supplier.setModifiedDate(new Date());
            supplier.setCode(randomCode());
            supplier.setDeleted(false);
            supplier.setDeletedAt(0L);
            supplier.setColabDate(colabDate);
    
            CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService.uploadMedia(image, "crm/suppliers");
            Map<String, Object> uploadResult = uploadFuture.join();
            uploadedPublicId = (String) uploadResult.get("public_id");
            String imageUrl = (String) uploadResult.get("secure_url");

            Image supplierImage = Image.builder()
                            .imageUrl(imageUrl)
                            .publicId(uploadedPublicId)
                            .referenceId(supplier.getId())
                            .referenceType("SUPPLIER")
                            .altText(supplier.getName())
                            .type("IMAGE")
                            .build();

            supplierImage.setInActive(active);
            supplierImage.setCreatedDate(new Date());
            supplierImage.setModifiedDate(new Date());
            supplierImage.setCode(randomCode());
            supplierImage.setDeletedAt(0L);
            supplierImage.setDeleted(false);

            supplier.setImage(supplierImage);

            iSupplierRepository.save(supplier);

            return new APIResponse<>(true, "Supplier created successfully");
        } catch (Exception ex) {
            if (uploadedPublicId != null) {
                try {
                    cloudinaryService.deleteMedia(uploadedPublicId).join();
                } catch (Exception deleteEx) {
                    System.err.println("Failed to cleanup Cloudinary image: " + uploadedPublicId);
                }
            }
            // Throw lỗi để @Transactional thực hiện rollback Database
            throw new RuntimeException("Failed to create supplier: " + ex.getMessage());
        }
    }

    private boolean isValidDate(Integer y, Integer m, Integer d) {
        try {
            LocalDate.of(y, m, d);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public APIResponse<Boolean> deleteSupplier(String id) {
        Supplier supplier = iSupplierRepository.findById(UUID.fromString(id)).orElseThrow(() -> new IllegalArgumentException("Supplier not found with id:  + id"));
        Image supllierImage = supplier.getImage();
        if (supllierImage != null && supllierImage.getPublicId() != null) {
            supllierImage.setInActive(false);
            supllierImage.setDeleted(true);
            supllierImage.setDeletedAt(System.currentTimeMillis() / 1000);
        }

        supplier.setInActive(false);
        supplier.setDeleted(true);
        supplier.setDeletedAt(System.currentTimeMillis() / 1000);
        iSupplierRepository.save(supplier);

        return new APIResponse<>(true, "Supplier deleted successfully");
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanSupplierTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60; // 2 phút xóa
        int warningMinutes = 1; // 1 phút cảnh báo

        long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;
        cleanTrash(iSupplierRepository, 
            SupplierSpecification.warningThreshold(warningThreshold),
            SupplierSpecification.deleteThreshold(deleteThreshold), 
            warningMinutes, 
            "SUPPLIER", 
            (supplier) -> {
                String publicId = supplier.getImage().getPublicId();
                if (publicId != null && !publicId.isEmpty()) {
                    cloudinaryService.deleteMedia(publicId);
                }
            });
    }

    @Override
    public PagingResponse<SupplierResponse> getAllSupplierTrash(int page, int limit, String sortBy, String direction, SupplierFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    SupplierSpecification.getAllSupplierTrashFilter(filter), 
                    SupplierResponse.class, 
                    iSupplierRepository);
    }

    @Override
    public APIResponse<Boolean> updateSupplier(String id, SupplierRequest supplierRequest, MultipartFile image, boolean active) {
        Map<String, Object> uploadImage = null;
        try {

            LocalDate colabDate = null;

            Integer day = supplierRequest.getStartDay();
            Integer month = supplierRequest.getStartMonth();
            Integer year = supplierRequest.getStartYear();

            if (day != null && month != null && year != null) {
                colabDate = LocalDate.of(year, month, day);
            }
            Supplier supplier = iSupplierRepository.findById(UUID.fromString(id)).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
            modelMapper.map(supplierRequest, supplier);
            supplier.setModifiedDate(new Date());
            supplier.setInActive(active);
            supplier.setColabDate(colabDate);
            String oldImage = (supplier.getImage() != null) ? supplier.getImage().getPublicId() : null; 

            if (image != null && !image.isEmpty()) {
                try {
                    uploadImage = cloudinaryService.uploadMedia(image, "crm/suppliers").join();
                } catch (Exception e) {
                    // Nếu upload thất bại, ta ném lỗi để dừng hàm, giữ nguyên ảnh cũ
                    throw new RuntimeException("If uploading a new image fails, the system will retain the old image. Detail: " + e.getMessage());
                }
                if (uploadImage != null) {
                    Image media = supplier.getImage();
                    if (media == null) {
                        media = Image.builder()
                            .imageUrl((String) uploadImage.get("secure_url"))
                            .publicId((String) uploadImage.get("public_id"))
                            .referenceId(supplier.getId())
                            .referenceType("SUPPLIER")
                            .altText(supplier.getName())
                            .type("IMAGE")
                            .build();
                        }else {
                            // UPDATE MEDIA CŨ
                            media.setImageUrl((String) uploadImage.get("secure_url"));
                            media.setPublicId((String) uploadImage.get("public_id"));
                            media.setAltText(supplier.getName());
                        }
                    media.setInActive(active);
                    media.setModifiedDate(new Date());
                    supplier.setImage(media);
                }
            }

            iSupplierRepository.save(supplier);

            if (oldImage != null && uploadImage != null) {
                final String finalDeleteId = oldImage;
                CompletableFuture.runAsync(() -> {
                    cloudinaryService.deleteMedia(finalDeleteId);
                });
            }
            return new APIResponse<>(true, "Updated supplier successfully");
        } catch (Exception e) {
            // --- ROLLBACK: Nếu DB lỗi, xóa ảnh mới vừa up lên Cloudinary ---
            if (uploadImage != null) {
                String publicIdRolback = (String) uploadImage.get("public_id");
                cloudinaryService.deleteMedia(publicIdRolback);
                System.err.println("Database error. Rolled back uploaded image: ");
            }
            return new APIResponse<>(false, "Update failed: " + e.getMessage());
        }
        
    }
    
}
