package com.charmroom.charmroom.service;

import com.charmroom.charmroom.entity.Ad;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.AdRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final ImageRepository imageRepository;
    private final CharmroomUtil.Upload uploadUtil;

    public Ad create(String title, String link, MultipartFile imageFile, LocalDateTime startTime, LocalDateTime endTime) {
        Image image = uploadUtil.buildImage(imageFile);
        Image savedImage = imageRepository.save(image);

        Ad ad = Ad.builder()
                .title(title)
                .link(link)
                .image(savedImage)
                .start(startTime)
                .end(endTime)
                .build();

        return adRepository.save(ad);
    }

    public Page<Ad> getAllAdsByPageable(Pageable pageable) {
        return adRepository.findAll(pageable);
    }

    @Transactional
    public Ad updateTitle(Integer adId, String newAdTitle) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() ->
                        new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateTitle(newAdTitle);
        return ad;
    }

    @Transactional
    public Ad updateLink(Integer adId, String newAdLink) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() ->
                        new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateLink(newAdLink);
        return ad;
    }

    @Transactional
    public Ad updateImage(Integer adId, MultipartFile imageFile) {
        Image image = uploadUtil.buildImage(imageFile);
        Image savedImage = imageRepository.save(image);

        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateImage(savedImage);
        return ad;
    }

    @Transactional
    public Ad updateStartTime(Integer adId, LocalDateTime newStartTime) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateStart(newStartTime);
        return ad;
    }

    @Transactional
    public Ad updateEndTime(Integer adId, LocalDateTime newEndTime) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        ad.updateEnd(newEndTime);
        return ad;
    }

    public void deleteAd(Integer adId) {
        Ad ad = adRepository.findById(adId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_AD, "adId: " + adId));

        adRepository.delete(ad);
    }
}

