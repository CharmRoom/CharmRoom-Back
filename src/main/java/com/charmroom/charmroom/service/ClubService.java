package com.charmroom.charmroom.service;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.util.CharmroomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final ImageRepository imageRepository;
    private final CharmroomUtil.Upload uploadUtil;

    public Club createClub(String clubName, String description, String contact) {
        if(isDuplicateClubName(clubName)) throw new BusinessLogicException(BusinessLogicError.DUPLICATED_CLUBNAME);

        Club club = Club.builder()
                .name(clubName)
                .description(description)
                .contact(contact)
                .build();

        return clubRepository.save(club);
    }

    public boolean isDuplicateClubName(String clubName) {
        return clubRepository.existsByName(clubName);
    }

    public Page<Club> getAllClubsByPageable(PageRequest pageRequest) {
        return clubRepository.findAll(pageRequest);
    }

    public Club getClub(Integer clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() ->
                        new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId)
                );
    }

    @Transactional
    public Club updateClubName(Integer clubId, String newClubName) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId)
        );

        club.updateName(newClubName);
        return club;
    }

    @Transactional
    public Club updateDescription(Integer clubId, String newClubDescription) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        club.updateDescription(newClubDescription);
        return club;
    }

    public Club updateContact(Integer clubId, String newClubContact) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        club.updateContact(newClubContact);
        return club;
    }

    public void deleteClub(Integer clubId) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        clubRepository.delete(club);
    }

    public Club setImage(String clubName, MultipartFile imageFile) {
        Club club = clubRepository.findByName(clubName).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubName: " + clubName));

        Image image = uploadUtil.buildImage(imageFile);
        Image saved = imageRepository.save(image);

        club.updateImage(saved);
        return club;
    }
}
