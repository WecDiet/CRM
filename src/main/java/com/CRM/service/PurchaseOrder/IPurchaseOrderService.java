package com.CRM.service.PurchaseOrder;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.enums.RestoreEnum;
import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderDetailResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderResponse;

public interface IPurchaseOrderService {
    PagingResponse<PurchaseOrderResponse> getAllPurchaseOrder(int page, int limit, String sortBy, String direction, boolean active, PurchaseOrderFilterRequest filter);
    
    APIResponse<PurchaseOrderDetailResponse> getPurchaseOrderDetail(String id);
    
    APIResponse<Boolean> createPurchaseOrder(PurchaseOrderRequest purchaseOrderRequest, List<MultipartFile> images, boolean active) throws BadRequestException;
    
    APIResponse<Boolean> updatePurchaseOrder(String id, PurchaseOrderRequest purchaseOrderRequest, boolean active);
    
    APIResponse<Boolean> deletePurchaseOrder(String id);
    
    PagingResponse<PurchaseOrderResponse> getAllPurchaseOrderTrash(int page, int limit, String sortBy, String direction, PurchaseOrderFilterRequest filter);
    
    APIResponse<PurchaseOrderDetailResponse> getPurchaseOrderTrashDetail(String id);
    
    void autoCleanPurchaseOrderTrash();
    
    APIResponse<Boolean> restorePurchaseOrder(String id, RestoreEnum action);
    
    APIResponse<Boolean> completePurchaseOrder(String poCode, String status, String type, String note);
}
