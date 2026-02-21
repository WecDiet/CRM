package com.CRM.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.request.Voucher.VoucherFilterRequest;
import com.CRM.request.Voucher.VoucherRequest;
import com.CRM.service.Voucher.VoucherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Voucher.BASE)
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<?> getAllVouchers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int limit,
        @RequestParam(defaultValue = "createdDate") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(defaultValue = "true") boolean active,
        @ModelAttribute VoucherFilterRequest filter
    ){
        return ResponseEntity.ok(voucherService.getAllVouchers(page, limit, sortBy, direction, active, filter));
    }

    @GetMapping(Enpoint.Voucher.ID)
    public ResponseEntity<?> getVoucherDetail(@PathVariable String id){
        return ResponseEntity.ok(voucherService.getVoucherDetail(id));
    }

    @PostMapping(Enpoint.Voucher.CREATE)
    public ResponseEntity<?> createVoucher(
        @ModelAttribute VoucherRequest voucherRequest,
        @RequestParam("active") boolean active,
        @RequestParam("media") MultipartFile image
    ) throws BadRequestException{
        return ResponseEntity.ok(voucherService.createVoucher(voucherRequest, active, image));
    }

}
