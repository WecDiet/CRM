package com.CRM.service.Warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.enums.RestoreEnum;
import com.CRM.model.Image;
import com.CRM.model.Warehouse;
import com.CRM.repository.IMediaRepository;
import com.CRM.repository.IPurchaseOrderRepository;
import com.CRM.repository.IWarehouseRepository;
import com.CRM.repository.Specification.WarehouseSpecification;
import com.CRM.request.Warehouse.WarehouseRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Warehouse.WarehouseResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseService extends HelperService<Warehouse, UUID> implements IWarehouseService {

    private final IWarehouseRepository iWarehouseRepository;

    private final ModelMapper modelMapper;

    private final IMediaRepository iMediaRepository;

    private final CloudinaryService cloudinaryService;

    private final IPurchaseOrderRepository iPurchaseOrderRepository;

    @Override
    public PagingResponse<WarehouseResponse> getAllWarehouses(int page, int limit, String sortBy, String direction,
            boolean active, WarehouseRequest filter) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                WarehouseSpecification.getAllWarehouseFilter(filter, active),
                WarehouseResponse.class,
                iWarehouseRepository);
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createWarehouse(WarehouseRequest warehouseRequest,
            boolean active,
            List<MultipartFile> images) {
        if (warehouseRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty.");
        }
        if (iWarehouseRepository.existsActiveByName(warehouseRequest.getName())) {
            throw new IllegalArgumentException("Warehouse name already exists.");
        }
        if (warehouseRequest.getWarehouseType().isEmpty()) {
            throw new IllegalArgumentException("Warehouse type cannot be empty.");
        }

        try {
            Warehouse warehouse = modelMapper.map(warehouseRequest, Warehouse.class);
            warehouse.setWarehouseType(warehouseRequest.getWarehouseType());
            warehouse.setInActive(active);
            warehouse.setCreatedDate(new Date());
            warehouse.setModifiedDate(new Date());
            warehouse.setCode(randomCode());
            warehouse.setDeletedAt(0L);
            warehouse.setDeleted(false);

            List<Image> imageList = new ArrayList<>();

            if (images != null && !images.isEmpty()) {
                if (images.size() > 5) {
                    throw new IllegalArgumentException("You can upload a maximum of 5 images.");
                }
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService
                                .uploadImage(image, "crm/warehouses");
                        Map<String, Object> uploadResult = uploadFuture.join();
                        String uploadedPublicId = (String) uploadResult.get("public_id");
                        String mediaUrl = (String) uploadResult.get("secure_url");

                        Image media = Image.builder()
                                .imageUrl(mediaUrl)
                                .publicId(uploadedPublicId)
                                .referenceType("WAREHOUSE")
                                .referenceId(warehouse.getId())
                                .altText(warehouse.getName())
                                .type("IMAGE")
                                .build();

                        media.setInActive(active);
                        media.setCreatedDate(new Date());
                        media.setModifiedDate(new Date());
                        media.setCode(randomCode());
                        media.setDeletedAt(0L);
                        media.setDeleted(false);

                        imageList.add(media);
                    }

                }
            }
            if (!imageList.isEmpty()) {
                warehouse.setImages(imageList);
                iWarehouseRepository.save(warehouse);
            }
            return new APIResponse<>(true, "Warehouse created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating warehouse: " + e.getMessage());
        }

    }

    @Override
    public APIResponse<Boolean> updateWarehouse(String id, boolean active, WarehouseRequest warehouseRequest,
            List<MultipartFile> images, List<String> idsImageDelete) {

        List<Map<String, Object>> uploadedResults = new ArrayList<>();
        
        List<String> finalImageDeleteIds = new ArrayList<>();

        try {
            Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + id));

            if (warehouseRequest.getName().isEmpty()) {
                throw new IllegalArgumentException("Warehouse name cannot be empty.");
            }

            if (warehouseRequest.getWard().isEmpty()) {
                throw new IllegalArgumentException("Ward cannot be empty.");
            }

            int currentImageSize = (warehouse.getImages() != null) ? warehouse.getImages().size() : 0;
            int imageDeleteSize = (idsImageDelete != null) ? idsImageDelete.size() : 0;
            int newImageSize = (images != null) ? images.size() : 0;

            if ((currentImageSize - imageDeleteSize + newImageSize) > 5 ) {
                throw new IllegalArgumentException("The total number of photos after updating must not exceed 5.");
            }

            if (images != null && !images.isEmpty()) {
                List<CompletableFuture<Map<String, Object>>> futures = images.stream()
                                    .filter(image -> !image.isEmpty())
                                    .map(image -> cloudinaryService.uploadImage(image, "crm/warehouses"))
                                    .collect(Collectors.toList());
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                uploadedResults = futures.stream()
                                .map(CompletableFuture::join)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
            }

            modelMapper.map(warehouseRequest, warehouse);
            warehouse.setInActive(active);
            warehouse.setModifiedDate(new Date());


            // Xóa ảnh cũ trong List của Entity (Hibernate sẽ tự đánh dấu xóa trong DB)
            if (warehouse.getImages() != null && idsImageDelete != null && !idsImageDelete.isEmpty()) {
                // Lọc ra những ảnh thực sự có trong DB để chuẩn bị xóa trên Cloudinary
                finalImageDeleteIds = warehouse.getImages().stream()
                                .map(Image::getPublicId)
                                .filter(idsImageDelete::contains)
                                .collect(Collectors.toList());
                
                // Xóa khỏi danh sách liên kết
                warehouse.getImages().removeIf(image -> idsImageDelete.contains(image.getPublicId()));
            }

            if (!uploadedResults.isEmpty()) {
                if (warehouse.getImages() == null) {
                    warehouse.setImages(new ArrayList<>());
                }
                
                for(Map<String, Object> uploadImages : uploadedResults){
                    Image newImage = Image.builder()
                                    .imageUrl((String) uploadImages.get("secure_url"))
                                    .publicId((String) uploadImages.get("public_id"))
                                    .referenceId(warehouse.getId())
                                    .referenceType("WAREHOUSE")
                                    .altText(warehouse.getName())
                                    .type("IMAGE")
                                    .build();
                    warehouse.getImages().add(newImage);
                }

                iWarehouseRepository.save(warehouse);

                if (!finalImageDeleteIds.isEmpty()) {
                    final List<String> finalDeleteIds = new ArrayList<>(finalImageDeleteIds);
                    CompletableFuture.runAsync(() -> {
                        finalDeleteIds.forEach(cloudinaryService::deleteImage);
                    }).exceptionally(ex -> {
                        System.err.println("Cloudinary delete failed: " + ex.getMessage());
                        return null;
                    });
                }
            }

            return new APIResponse<>(true, "Updated Warehouse successfully");
        } catch (Exception e) {
            if (!uploadedResults.isEmpty()) {
                uploadedResults.forEach(image -> cloudinaryService.deleteImage((String) image.get("public_id")));
            }
            throw new RuntimeException("Update warehouse failed: " + e.getMessage());
        }
    }

    @Override
    public APIResponse<Boolean> deleteWarehouse(String id) {
        Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + id));
        List<Image> warehouseImages = warehouse.getImages();
        if (warehouseImages != null && !warehouseImages.isEmpty()) {
            warehouse.getImages().stream().forEach(images -> {
                images.setInActive(false);
                images.setDeleted(true);
                images.setDeletedAt(System.currentTimeMillis() / 1000);
                iMediaRepository.save(images);
            });

        }
        warehouse.setInActive(false);
        warehouse.setDeleted(true);
        warehouse.setDeletedAt(System.currentTimeMillis() / 1000);
        iWarehouseRepository.save(warehouse);
        return new APIResponse<>(true, "Warehouse deleted successfully.");
    }

    @Override
    public PagingResponse<WarehouseResponse> getAllWarehouseTrash(int page, int limit, String sortBy, String direction,
            WarehouseRequest filter) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                WarehouseSpecification.getAllWarehouseTrashFilter(filter),
                WarehouseResponse.class,
                iWarehouseRepository);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // Quét mõi 1 phút / 1 lần
    public void autoCleanWarehouseTrash() {
        long currentTime = System.currentTimeMillis() / 1000;
        long duration = 2L * 60;
        int warningMinutes = 1;
        Long warningThreshold = currentTime - (duration - (warningMinutes * 60L));
        long deleteThreshold = currentTime - duration;

        cleanTrash(iWarehouseRepository,
                WarehouseSpecification.warningThreshold(warningThreshold),
                WarehouseSpecification.deleteThreshold(deleteThreshold), warningMinutes,
                "WAREHOUSE",
                (warehouse) -> {
                    List<Image> images = warehouse.getImages();
                    AtomicInteger success = new AtomicInteger();
                    AtomicInteger failed = new AtomicInteger();
                    List<CompletableFuture<Void>> futures = images.stream()
                            .map(Image::getPublicId)
                            .filter(Objects::nonNull)
                            .map(publicId -> cloudinaryService.deleteImage(publicId).exceptionally(ex -> {
                                failed.incrementAndGet();
                                System.out.println("Failed to delete media with public ID: " + publicId + ". Error: "
                                        + ex.getMessage());
                                return null;
                            })).toList();
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                    if (success.get() > 0) {
                        images.clear();
                    } 
                });
    }

    @Override
    public APIResponse<Boolean> restoreWarehouse(String id, RestoreEnum action) {
        // Tìm bản ghi trong thùng rác
        Warehouse warehouseInTrash = iWarehouseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("This warehouse doesn't belong in the trash can."));

        // Tìm bản ghi trùng đang hoạt động
        Optional<Warehouse> activeDuplicate = iWarehouseRepository.findActiveByName(warehouseInTrash.getName());

        if (activeDuplicate.isPresent()) {
            // Nếu user chưa xác nhận hành động (lần gọi đầu tiên)
            if (action == null || action == RestoreEnum.RESTORE) {
                throw new IllegalArgumentException("CONFLICT: A warehouse with this name already exists.");
            }

            // Nếu user chọn Bỏ qua
            if (action == RestoreEnum.CANCEL) {
                return new APIResponse<>(false, "Restore operation was cancelled.");
            }

            // Nếu user chọn Ghi đè (OVERWRITE)
            if (action == RestoreEnum.OVERWRITE) {
                // Xóa warehouse đang active trước
                iWarehouseRepository.delete(activeDuplicate.get());
                iWarehouseRepository.flush(); // Xóa ngay để tránh trùng Unique Key khi save bên dưới
            }
        }
        warehouseInTrash.setInActive(true);
        warehouseInTrash.setDeleted(false);
        warehouseInTrash.setDeletedAt(0L);

        iWarehouseRepository.save(warehouseInTrash);
        return APIResponse.<Boolean>builder()
                .message("Restored successfully")
                .data(true)
                .build();
    }


}
