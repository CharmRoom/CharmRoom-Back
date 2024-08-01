package com.charmroom.charmroom.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.dto.business.AdDto;
import com.charmroom.charmroom.dto.business.AdMapper;
import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.AdRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final ImageRepository imageRepository;
    private final CharmroomUtil.Upload uploadUtil;

    public AdDto create(String title, String link, LocalDateTime startTime, LocalDateTime endTime, MultipartFile imageFile) {
        Image image = uploadUtil.buildImage(imageFile);
        Image savedImage = imageRepository.save(image);

        Ad ad = Ad.builder()
                .title(title)
                .link(link)
                .start(startTime)
                .end(endTime)
                .image(savedImage)
                .build();

        Ad saved = adRepository.save(ad);
        return AdMapper.toDto(saved);
    }

    public Page<AdDto> getAllAdsByPageable(Pageable pageable) {
        Page<Ad> ads = adRepository.findAll(pageable);
        return ads.map(AdMapper::toDto);
    }

    @Transactional
    public AdDto updateTitle(Integer adId, String newAdTitle) {
        Ad ad = getAd(adId);
        ad.updateTitle(newAdTitle);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateLink(Integer adId, String newAdLink) {
        Ad ad = getAd(adId);
        ad.updateLink(newAdLink);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto setImage(Integer adId, MultipartFile imageFile) {
        Ad ad = getAd(adId);
        Image adImage = buildImage(imageFile, ad);
        ad.updateImage(adImage);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateStartTime(Integer adId, LocalDateTime newStartTime) {
        Ad ad = getAd(adId);
        ad.updateStart(newStartTime);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateEndTime(Integer adId, LocalDateTime newEndTime) {
        Ad ad = getAd(adId);
        ad.updateEnd(newEndTime);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateAd(Integer adId, String title, String link, LocalDateTime startTime, LocalDateTime endTime, MultipartFile imageFile) {
        Ad ad = getAd(adId);
        Image adImage = buildImage(imageFile, ad);

        ad.updateTitle(title);
        ad.updateLink(link);
        ad.updateStart(startTime);
        ad.updateEnd(endTime);
        ad.updateImage(adImage);

        return AdMapper.toDto(ad);
    }

    public void deleteAd(Integer adId) {
        Ad ad = getAd(adId);
        adRepository.delete(ad);
    }

    private Ad getAd(Integer adId) {
        return adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));
    }

    private Image buildImage(MultipartFile imageFile, Ad ad) {
        if (ad.getImage() != null) {
            imageRepository.delete(ad.getImage());
        }

        Image image = uploadUtil.buildImage(imageFile);
        return imageRepository.save(image);
    }
}

