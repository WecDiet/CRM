package com.CRM.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.TransferTicket.TransferTicketFilterRequest;
import com.CRM.request.TransferTicket.TransferTicketRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.TransferTicket.BaseTransferTicketResponse;
import com.CRM.response.TransferTicket.TransferProductResponse;
import com.CRM.response.TransferTicket.TransferTicketDetailResponse;
import com.CRM.service.TransferTicket.TransferTicketService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Enpoint.TransferTicket.BASE)
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

    @GetMapping(Enpoint.TransferTicket.ID)
    public ResponseEntity<APIResponse<TransferTicketDetailResponse>> getTransferTicketDetail(@PathVariable String id){
        return ResponseEntity.ok(transferTicketService.getTransferTicketDetail(id));
    }

    @PostMapping(Enpoint.TransferTicket.CREATE)
    public ResponseEntity<APIResponse<Boolean>> createTransferTicket(@RequestBody @RequestPart("data") TransferTicketRequest transferTicketRequest){
        return ResponseEntity.ok(transferTicketService.createTransferTicket(transferTicketRequest));
    }

    @GetMapping(Enpoint.TransferTicket.CREATE)
    public List<TransferProductResponse> getProductsForNewTicket(@RequestParam String keyword){
        return transferTicketService.getProductsForNewTicket(keyword);
    }
}
