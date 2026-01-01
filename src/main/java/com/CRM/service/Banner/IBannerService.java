package com.CRM.service.Banner;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.Banner.bannerRequest;
import com.CRM.response.Banner.BannerResponse;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;

public interface IBannerService {
    PagingResponse<BannerResponse> getAllBanners(int page, int limit, String sortBy, String direction);

    APIResponse<Boolean> createBanner(bannerRequest bannerRequest, MultipartFile media, int width, int height)
            throws NotFoundException;

    APIResponse<Boolean> updateBanner(Long id, bannerRequest bannerRequest) throws NotFoundException;

    APIResponse<Boolean> deleteBanner(Long id) throws NotFoundException;

    APIResponse<Boolean> uploadMediaBanner(String bannerID, MultipartFile media) throws NotFoundException;
}
