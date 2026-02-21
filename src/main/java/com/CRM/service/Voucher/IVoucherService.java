package com.CRM.service.Voucher;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Voucher.VoucherFilterRequest;
import com.CRM.request.Voucher.VoucherRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Voucher.VoucherDetailResponse;
import com.CRM.response.Voucher.VoucherResponse;

public interface IVoucherService {

    PagingResponse<VoucherResponse> getAllVouchers(int page, int limit, String sortBy, String direction, boolean active ,VoucherFilterRequest filter);

    APIResponse<VoucherDetailResponse> getVoucherDetail(String id);

    APIResponse<Boolean> createVoucher(VoucherRequest voucherRequest, boolean active, MultipartFile image) throws BadRequestException;
}
