package com.CRM.service.Warehouse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.enums.RestoreEnum;
import com.CRM.model.Media;
import com.CRM.model.Warehouse;
import com.CRM.repository.IMediaRepository;
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

    @Override
    public PagingResponse<WarehouseResponse> getAllWarehouses(int page, int limit, String sortBy, String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                WarehouseSpecification.getAllWarehouse(),
                WarehouseResponse.class,
                iWarehouseRepository);
    }

    @Override
    public APIResponse<Boolean> createWarehouse(WarehouseRequest warehouseRequest, List<MultipartFile> images,
            int width,
            int height) {
        if (warehouseRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty.");
        }
        if (iWarehouseRepository.existsActiveByName(warehouseRequest.getName())) {
            throw new IllegalArgumentException("Warehouse name already exists.");
        }

        try {
            Warehouse warehouse = modelMapper.map(warehouseRequest, Warehouse.class);
            warehouse.setInActive(warehouseRequest.isActive());
            warehouse.setCreatedDate(new Date());
            warehouse.setModifiedDate(new Date());
            warehouse.setCode(randomCode());
            warehouse.setDeletedAt(0L);
            warehouse.setDeleted(false);

            List<Media> imageList = new ArrayList<>();

            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        Map uploadResult = cloudinaryService.uploadMedia(image, "crm/warehouses", width, height);
                        String uploadedPublicId = (String) uploadResult.get("public_id");
                        String mediaUrl = (String) uploadResult.get("secure_url");

                        Media media = Media.builder()
                                .imageUrl(mediaUrl)
                                .publicId(uploadedPublicId)
                                .referenceType("WAREHOUSE")
                                .referenceId(warehouse.getId())
                                .altText(warehouse.getName())
                                .type("IMAGE")
                                .build();

                        media.setInActive(warehouseRequest.isActive());
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
    public APIResponse<Boolean> updateWarehouse(String id, WarehouseRequest warehouseRequest,
            List<MultipartFile> images,
            int width, int height) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateWarehouse'");
    }

    @Override
    public APIResponse<Boolean> deleteWarehouse(String id) {
        Warehouse warehouse = iWarehouseRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + id));
        List<Media> warehouseImages = warehouse.getImages();
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
    public PagingResponse<WarehouseResponse> getAllWarehouseTrash(int page, int limit, String sortBy,
            String direction) {
        return getAll(
                page,
                limit,
                sortBy,
                direction,
                WarehouseSpecification.getAllWarehouseTrash(),
                WarehouseResponse.class,
                iWarehouseRepository);
    }

    @Override
    public void autoCleanWarehouseTrash() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'autoCleanWarehouseTrash'");
    }

    @Override
    public APIResponse<Boolean> restoreWarehouse(String id, RestoreEnum action) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'restoreWarehouse'");
    }

}
