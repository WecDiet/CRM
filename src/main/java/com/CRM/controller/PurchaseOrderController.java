package com.CRM.controller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;
import com.CRM.request.PurchaseOrder.PurchaseOrderRequest;
import com.CRM.request.PurchaseOrder.Delivery.ReceiveDeliveryRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderDetailResponse;
import com.CRM.service.PurchaseOrder.PurchaseOrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.PurchaseOrder.BASE)
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<?> getAllPurchaseOrder(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(defaultValue = "true") boolean active,
        @ModelAttribute PurchaseOrderFilterRequest filter) {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrder(page, limit, sortBy, direction, active, filter));
    }


    @GetMapping(Enpoint.PurchaseOrder.ID)
    public ResponseEntity<APIResponse<PurchaseOrderDetailResponse>> getPurchaseOrderDetail( @PathVariable String id ) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderDetail(id));
    }

    @PostMapping(
        value = Enpoint.PurchaseOrder.CREATE,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE

    )
    public ResponseEntity<APIResponse<Boolean>> createPurchaseOrder(
        @RequestPart("data") PurchaseOrderRequest purchaseOrderRequest,
        @RequestParam("images") List<MultipartFile> images,
        @RequestParam(defaultValue = "true") boolean active
    ) throws BadRequestException {
        return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(purchaseOrderRequest, images, active));
    }

    @DeleteMapping(Enpoint.PurchaseOrder.DELETE)
    public ResponseEntity<APIResponse<Boolean>> deletePurchaseOrder(@PathVariable String id) {
        return ResponseEntity.ok(purchaseOrderService.deletePurchaseOrder(id));
    }

    @GetMapping(Enpoint.PurchaseOrder.TRASH)
    public ResponseEntity<?> getAllPurchaseOrderTrash(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute PurchaseOrderFilterRequest filter) {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrderTrash(page, limit, sortBy, direction, filter));
    }

    @GetMapping(Enpoint.PurchaseOrder.TRASH_ID)
    public ResponseEntity<APIResponse<PurchaseOrderDetailResponse>> getPurchaseOrderTrashDetail(@PathVariable String id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderTrashDetail(id));
    }

    
    @PutMapping(Enpoint.PurchaseOrder.CONFIRM)
    public ResponseEntity<APIResponse<Boolean>> confirmPurchaseOrder(@PathVariable String id) {
        return ResponseEntity.ok(purchaseOrderService.confirmOrder(id));
    }


    @PostMapping(Enpoint.PurchaseOrder.RECEIVE)
    public ResponseEntity<?> receive(
        @PathVariable("code") String code,
        @RequestPart("receiveDeliveryRequest") ReceiveDeliveryRequest receiveDeliveryRequest,
        @RequestPart("images") List<MultipartFile> images
    ){
        return ResponseEntity.ok(purchaseOrderService.receiveDelivery(code, receiveDeliveryRequest, images));
    }

}