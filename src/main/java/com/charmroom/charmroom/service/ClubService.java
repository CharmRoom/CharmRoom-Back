package com.charmroom.charmroom.service;

import com.charmroom.charmroom.dto.business.ClubDto;
import com.charmroom.charmroom.dto.business.ClubMapper;
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

    public ClubDto createClub(ClubDto clubDto, MultipartFile imageFile) {
        if(isDuplicateClubName(clubDto.getName())) throw new BusinessLogicException(BusinessLogicError.DUPLICATED_CLUBNAME);

        Image clubImage = null;
        if (imageFile != null) {
            Image image = uploadUtil.buildImage(imageFile);
            clubImage = imageRepository.save(image);
        }

        Club club = Club.builder()
                .name(clubDto.getName())
                .description(clubDto.getDescription())
                .contact(clubDto.getContact())
                .image(clubImage)
                .build();

        Club saved = clubRepository.save(club);
        return ClubMapper.toDto(saved);
    }

    public ClubDto createClub(ClubDto clubDto) {
        return createClub(clubDto, null);
    }

    public boolean isDuplicateClubName(String clubName) {
        return clubRepository.existsByName(clubName);
    }

    public Page<ClubDto> getAllClubsByPageable(PageRequest pageRequest) {
        Page<Club> clubs = clubRepository.findAll(pageRequest);
        return clubs.map(club -> ClubMapper.toDto(club));
    }

    public ClubDto getClub(Integer clubId) {
        Club found = clubRepository.findById(clubId)
                .orElseThrow(() ->
                        new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId)
                );
        return ClubMapper.toDto(found);
    }

    @Transactional
    public ClubDto updateClubName(Integer clubId, String newClubName) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId)
        );

        club.updateName(newClubName);
        return ClubMapper.toDto(club);
    }

    @Transactional
    public ClubDto updateDescription(Integer clubId, String newClubDescription) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        club.updateDescription(newClubDescription);
        return ClubMapper.toDto(club);
    }

    public ClubDto updateContact(Integer clubId, String newClubContact) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        club.updateContact(newClubContact);
        return ClubMapper.toDto(club);
    }

    public void deleteClub(Integer clubId) {
        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        clubRepository.delete(club);
    }

    @Transactional
    public ClubDto setImage(String clubName, MultipartFile imageFile) {
        Club club = clubRepository.findByName(clubName).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubName: " + clubName));

        if (club.getImage() != null) {
            uploadUtil.deleteFile(club.getImage());
        }

        Image image = uploadUtil.buildImage(imageFile);
        Image saved = imageRepository.save(image);

        club.updateImage(saved);
        return ClubMapper.toDto(club);
    }
}
