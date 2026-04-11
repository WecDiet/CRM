package com.CRM.service.Store;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Store.StoreFilterRequest;
import com.CRM.request.Store.StoreRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Store.BaseStoreResponse;

public interface IStoreService {
    
    PagingResponse<BaseStoreResponse> getAllStore(int page, int limit, String sortBy, String direction, boolean active, StoreFilterRequest filter);

    APIResponse<Boolean> createStore(StoreRequest storeRequest, List<MultipartFile> images);
}
