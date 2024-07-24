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
    private final CharmroomUtil charmroomUtil;

    public AdDto create(String title, String link, MultipartFile imageFile, LocalDateTime startTime, LocalDateTime endTime) {
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
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() ->
                        new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateTitle(newAdTitle);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateLink(Integer adId, String newAdLink) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() ->
                        new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateLink(newAdLink);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto setImage(Integer adId, MultipartFile imageFile) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        Image adImage = null;
        if (ad.getImage() != null) {
            imageRepository.delete(ad.getImage());
        }

        Image image = uploadUtil.buildImage(imageFile);
        adImage = imageRepository.save(image);

        ad.updateImage(adImage);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateStartTime(Integer adId, LocalDateTime newStartTime) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateStart(newStartTime);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateEndTime(Integer adId, LocalDateTime newEndTime) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateEnd(newEndTime);
        return AdMapper.toDto(ad);
    }

    @Transactional
    public AdDto updateAd(Integer adId, String title, String link, LocalDateTime startTime, LocalDateTime endTime, MultipartFile imageFile) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        Image adImage = null;
        if (ad.getImage() != null) {
            imageRepository.delete(ad.getImage());
        }

        Image image = uploadUtil.buildImage(imageFile);
        adImage = imageRepository.save(image);

        ad.updateTitle(title);
        ad.updateLink(link);
        ad.updateStart(startTime);
        ad.updateEnd(endTime);
        ad.updateImage(adImage);

        return AdMapper.toDto(ad);
    }

    public void deleteAd(Integer adId) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        adRepository.delete(ad);
    }
}

