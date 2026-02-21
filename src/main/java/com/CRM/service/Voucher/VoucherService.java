package com.CRM.service.Voucher;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Media;
import com.CRM.model.Voucher;
import com.CRM.repository.IProductRepository;
import com.CRM.repository.IVoucherRepository;
import com.CRM.repository.Specification.VoucherSpecification;
import com.CRM.request.Voucher.VoucherFilterRequest;
import com.CRM.request.Voucher.VoucherRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Voucher.VoucherDetailResponse;
import com.CRM.response.Voucher.VoucherResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherService extends HelperService<Voucher, UUID> implements IVoucherService {

    private final IVoucherRepository iVoucherRepository;

    private final ModelMapper modelMapper;

    private final IProductRepository iProductRepository;

    private final CloudinaryService cloudinaryService;

    @Override
    public PagingResponse<VoucherResponse> getAllVouchers(int page, int limit, String sortBy, String direction,
            boolean active, VoucherFilterRequest filter) {
        return getAll(
                page, 
                limit, 
                sortBy, 
                direction, 
                VoucherSpecification.getAllVoucherFilter(filter, active), 
                VoucherResponse.class, 
                iVoucherRepository);
    }

    @Override
    public APIResponse<VoucherDetailResponse> getVoucherDetail(String id) {
        return getById(
                UUID.fromString(id), 
                iVoucherRepository, 
                Voucher.class, 
                VoucherDetailResponse.class
            );
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createVoucher(VoucherRequest voucherRequest, boolean active,MultipartFile image) throws BadRequestException {
    
        if (iVoucherRepository.existsByCouponCode(voucherRequest.getCouponCode())) {
            throw new BadRequestException("The voucher code already exists.");
        }

        LocalDateTime startDate = LocalDateTime.of(voucherRequest.getStartYear(), voucherRequest.getStartMonth(), voucherRequest.getStartDay(), 0,0,0);
        
        LocalDateTime expirationDate = LocalDateTime.of(voucherRequest.getEndYear(), voucherRequest.getEndMonth(), voucherRequest.getEndDay(), 23, 59, 59);
        
        if (startDate.isAfter(expirationDate)) {
            throw new BadRequestException("The start time must be before the end time.");
        }
        
        if ("percentage".equalsIgnoreCase(voucherRequest.getDiscountType())
            && voucherRequest.getDiscount().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("Discount percentage must not exceed 100%.");
        }
    
        if (!voucherRequest.getIsGlobal() && (voucherRequest.getProductIds() == null || voucherRequest.getProductIds().isEmpty())) {
            throw new BadRequestException("Non-global vouchers must specify at least one product.");
        }
        String uploadedPublicId = null;
        try {
            Voucher voucher = modelMapper.map(voucherRequest, Voucher.class);
            voucher.setInActive(active);
            voucher.setStartDate(startDate);
            voucher.setExpirationDate(expirationDate);
            voucher.setUsedCount(0);
            voucher.setCreatedDate(new Date());
            voucher.setModifiedDate(new Date());
            voucher.setCouponCode(voucherRequest.getCouponCode());
            voucher.setCode(randomCode());
            voucher.setDeleted(false);
            voucher.setDeletedAt(0L);

            CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService.uploadMedia(image, "crm/vouchers");
            
            Map<String, Object> uploadResult = uploadFuture.join();
            uploadedPublicId = (String) uploadResult.get("public_id");
            String imageUrl = (String) uploadResult.get("secure_url");

            Media voucherImage = Media.builder()
                        .imageUrl(imageUrl)
                        .publicId(uploadedPublicId)
                        .referenceId(voucher.getId())
                        .referenceType("VOUCHER")
                        .altText(voucher.getName())
                        .type("IMAGE")
                        .build();
            voucherImage.setInActive(active);
            voucherImage.setCreatedDate(new Date());
            voucherImage.setModifiedDate(new Date());
            voucherImage.setCode(randomCode());
            voucherImage.setDeleted(false);
            voucherImage.setDeletedAt(0L);

            voucher.setImage(voucherImage);
            iVoucherRepository.save(voucher);

            return new APIResponse<>(true, "Voucher created successfully");
        } catch (Exception e) {
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
            throw new RuntimeException("Failed to create voucher: " + e.getMessage());
        }
    }
    
}
