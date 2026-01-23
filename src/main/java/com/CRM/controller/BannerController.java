package com.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.CRM.constant.Enpoint;
import com.CRM.request.Banner.BannerRequest;
import com.CRM.service.Banner.BannerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Banner.BASE)
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ResponseEntity<?> getAllBanners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "seq") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(bannerService.getAllBanners(page, limit, sortBy, direction, active));
    }

    @PostMapping(Enpoint.Banner.CREATE)
    public ResponseEntity<?> createBanner(
            @ModelAttribute BannerRequest bannerRequest,
            @RequestParam("media") MultipartFile media,
            @RequestParam("width") int width,
            @RequestParam("height") int height) throws NotFoundException {
        return ResponseEntity.ok(bannerService.createBanner(bannerRequest, media, width, height));
    }

    @DeleteMapping(Enpoint.Banner.DELETE)
    public ResponseEntity<?> deleteBanner(@PathVariable String id) {
        return ResponseEntity.ok(bannerService.deleteBanner(id));
    }

    @GetMapping(Enpoint.Banner.TRASH)
    public ResponseEntity<?> getAllBannerTrash(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(bannerService.getAllBannerTrash(page, limit, sortBy, direction));
    }

}
