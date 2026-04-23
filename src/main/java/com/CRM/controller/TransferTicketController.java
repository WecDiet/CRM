package com.CRM.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Endpoint;
import com.CRM.request.TransferTicket.TransferTicketFilterRequest;
import com.CRM.request.TransferTicket.TransferTicketRequest;
import com.CRM.request.TransferTicket.ConfirmReceipt.ConfirmTransferTicketRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.TransferTicket.BaseTransferTicketResponse;
import com.CRM.response.TransferTicket.TransferProductResponse;
import com.CRM.response.TransferTicket.TransferTicketDetailResponse;
import com.CRM.response.TransferTicket.ConfirmReceipt.ComfirmReceiptResponse;
import com.CRM.service.TransferTicket.TransferTicketService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Endpoint.TransferTicket.BASE)
@RequiredArgsConstructor
public class TransferTicketController {
    
    private final TransferTicketService transferTicketService;

    @GetMapping
    public ResponseEntity<PagingResponse<BaseTransferTicketResponse>> getAllTransferTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @ModelAttribute TransferTicketFilterRequest filter
    ){
        return ResponseEntity.ok(transferTicketService.getAllTransferTicket(page, limit, sortBy, direction, filter));
    }

    @GetMapping(Endpoint.TransferTicket.ID)
    public ResponseEntity<APIResponse<TransferTicketDetailResponse>> getTransferTicketDetail(@PathVariable String id){
        return ResponseEntity.ok(transferTicketService.getTransferTicketDetail(id));
    }

    @PostMapping(Endpoint.TransferTicket.CREATE)
    public ResponseEntity<APIResponse<Boolean>> createTransferTicket(@RequestBody @RequestPart("data") TransferTicketRequest transferTicketRequest){
        return ResponseEntity.ok(transferTicketService.createTransferTicket(transferTicketRequest));
    }

    @GetMapping(Endpoint.TransferTicket.CREATE)
    public List<TransferProductResponse> getProductsForNewTicket(@RequestParam String keyword){
        return transferTicketService.getProductsForNewTicket(keyword);
    }

    @PutMapping(Endpoint.TransferTicket.CONFIRM)
    public ResponseEntity<APIResponse<TransferTicketDetailResponse>> confirmTransferTicket(@PathVariable String id){
        return ResponseEntity.ok(transferTicketService.confirmSendingTransferTicket(id));
    }


    /** Đánh dấu đã nhận hàng (chưa cập nhật tồn kho) */
    @PutMapping(Endpoint.TransferTicket.MARK)
    public ResponseEntity<APIResponse<Boolean>> markReceived(@PathVariable String ticketCode){
        return ResponseEntity.ok(transferTicketService.markInTransit(ticketCode));
    }

    @PostMapping(Endpoint.TransferTicket.STORE_RECEIVED)
    public ResponseEntity<APIResponse<Boolean>> confirmReceived(
        @PathVariable String ticketCode, 
        @RequestBody ConfirmTransferTicketRequest request){
        return ResponseEntity.ok(transferTicketService.confirmReceivedAtDestination(ticketCode, request));
    }

    @GetMapping(Endpoint.TransferTicket.INFOR_RECEIPT)
    public ResponseEntity<APIResponse<ComfirmReceiptResponse>> getInforConfirmReceipt(@PathVariable String ticketCode){
        return ResponseEntity.ok(transferTicketService.getInforConfirmReceipt(ticketCode));
    }
}
