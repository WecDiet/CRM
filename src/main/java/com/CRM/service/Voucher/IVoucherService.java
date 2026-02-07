package com.CRM.service.Voucher;

import com.CRM.request.Voucher.VoucherRequest;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Voucher.VoucherResponse;

public interface IVoucherService {

    PagingResponse<VoucherResponse> getAllVouchers(int page, int limit, String sortBy, String direction, VoucherRequest filter);
    
}
