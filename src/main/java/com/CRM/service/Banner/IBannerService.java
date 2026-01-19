package com.CRM.service.Banner;

import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.enums.RestoreEnum;
import com.CRM.request.Banner.bannerRequest;
import com.CRM.response.Banner.BannerResponse;
import com.CRM.response.Brand.BrandResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IBannerService {
        PagingResponse<BannerResponse> getAllBanners(int page, int limit, String sortBy, String direction,
                        boolean active);

        APIResponse<Boolean> createBanner(bannerRequest bannerRequest, MultipartFile media, int width, int height);

        APIResponse<Boolean> updateBanner(String id, bannerRequest bannerRequest, MultipartFile media, int width,
                        int height);

        APIResponse<Boolean> deleteBanner(String id);

        PagingResponse<BannerResponse> getAllBannerTrash(int page, int limit, String sortBy, String direction);

        void autoCleanBannerTrash();

        APIResponse<Boolean> restoreBanner(String id, RestoreEnum action);
}
