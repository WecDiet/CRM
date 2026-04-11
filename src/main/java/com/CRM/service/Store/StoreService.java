package com.CRM.service.Store;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Image;
import com.CRM.model.Store;
import com.CRM.repository.IStoreRepository;
import com.CRM.repository.Specification.StoreSpecification;
import com.CRM.request.Store.StoreFilterRequest;
import com.CRM.request.Store.StoreRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Store.BaseStoreResponse;
import com.CRM.service.Cloudinary.CloudinaryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService extends HelperService<Store, UUID> implements IStoreService {

    private final IStoreRepository iStoreRepository;

    private final CloudinaryService cloudinaryService;

    private final ModelMapper modelMapper;
 
    @Override
    public PagingResponse<BaseStoreResponse> getAllStore(int page, int limit, String sortBy, String direction,
            boolean active, StoreFilterRequest filter) {
        return getAll(
                    page, 
                    limit, 
                    sortBy, 
                    direction, 
                    StoreSpecification.getAllStore(filter, active), 
                    BaseStoreResponse.class, 
                    iStoreRepository);
    }

    @Override
    @Transactional
    public APIResponse<Boolean> createStore(StoreRequest storeRequest, List<MultipartFile> images) {
        if (storeRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Store name cannot be empty");
        }

        if (iStoreRepository.existsActiveByName(storeRequest.getName())) {
            throw new IllegalArgumentException("Store name already exists.");
        }
        
        if (storeRequest.getStreet().isEmpty()) {
            throw new IllegalArgumentException("Street Store cannot be empty");
        }

        if (storeRequest.getWard().isEmpty()) {
            throw new IllegalArgumentException("Ward Store cannot be empty");
        }

        if (storeRequest.getDistrict().isEmpty()) {
            throw new IllegalArgumentException("District Store cannot be empty");
        }

        if (storeRequest.getCity().isEmpty()) {
            throw new IllegalArgumentException("City Store cannot be empty");
        }

        if (storeRequest.getCountry().isEmpty()) {
            throw new IllegalArgumentException("Country Store cannot be empty");
        }

        try {
            Store store = modelMapper.map(storeRequest, Store.class);
            store.setCode(randomCode());
            store.setInActive(true);
            store.setCreatedDate(new Date());
            store.setModifiedDate(new Date());
            store.setDeleted(false);
            store.setDeletedAt(0L);

            List<Image> imageList = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                if (images.size() > 5) {
                    throw new IllegalArgumentException("You can upload a maximum of 5 images.");
                }

                for (MultipartFile image : images){
                    if (!image.isEmpty()) {
                        CompletableFuture<Map<String, Object>> uploadFuture = cloudinaryService
                                .uploadImage(image, "crm/stores");
                        Map<String, Object> uploadResult = uploadFuture.join();
                        String uploadedPublicId = (String) uploadResult.get("public_id");
                        String mediaUrl = (String) uploadResult.get("secure_url");

                        Image file = Image.builder()
                                .imageUrl(mediaUrl)
                                .publicId(uploadedPublicId)
                                .referenceType("WAREHOUSE")
                                .referenceId(store.getId())
                                .altText(store.getName())
                                .type("IMAGE")
                                .build();
                        file.setInActive(true);
                        file.setCreatedDate(new Date());
                        file.setModifiedDate(new Date());
                        file.setCode(randomCode());
                        file.setDeletedAt(0L);
                        file.setDeleted(false);

                        imageList.add(file);
                    }
                }
            }
            if (!imageList.isEmpty()) {
                store.setImages(imageList);
                iStoreRepository.save(store);
            }
            return new APIResponse<>(true, "Created Store successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating store: " + e.getMessage());
        }
    }
    
}
