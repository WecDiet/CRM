package com.CRM.service.Voucher;

import java.util.UUID;

import com.CRM.Util.Helper.HelperService;
import com.CRM.model.Voucher;
import com.CRM.request.Voucher.VoucherRequest;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.Voucher.VoucherResponse;

public class VoucherService extends HelperService<Voucher, UUID> implements IVoucherService {

    @Override
    public PagingResponse<VoucherResponse> getAllVouchers(int page, int limit, String sortBy, String direction,
            VoucherRequest filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllVouchers'");
    }
    
}
