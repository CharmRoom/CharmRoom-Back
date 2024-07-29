package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.dto.business.ClubDto;
import com.charmroom.charmroom.dto.business.ClubMapper;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final ImageRepository imageRepository;
    private final CharmroomUtil.Upload uploadUtil;
    private final UserRepository userRepository;

    public ClubDto createClub(String username, ClubDto clubDto, MultipartFile imageFile) {
        User user = findUser(username);

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
                .owner(user)
                .build();

        Club saved = clubRepository.save(club);
        return ClubMapper.toDto(saved);
    }

    public ClubDto createClub(String username, ClubDto clubDto) {
        return createClub(username, clubDto, null);
    }

    public boolean isDuplicateClubName(String clubName) {
        return clubRepository.existsByName(clubName);
    }

    public Page<ClubDto> getAllClubsByPageable(Pageable pageable) {
        Page<Club> clubs = clubRepository.findAll(pageable);
        return clubs.map(ClubMapper::toDto);
    }

    public ClubDto getClub(Integer clubId) {
        Club found = findClub(clubId);
        return ClubMapper.toDto(found);
    }

    @Transactional
    public ClubDto updateClubName(Integer clubId, String newClubName) {
        Club club = findClub(clubId);
        club.updateName(newClubName);
        return ClubMapper.toDto(club);
    }

    @Transactional
    public ClubDto updateDescription(Integer clubId, String newClubDescription) {
        Club club = findClub(clubId);
        club.updateDescription(newClubDescription);
        return ClubMapper.toDto(club);
    }

    @Transactional
    public ClubDto updateContact(Integer clubId, String newClubContact) {
        Club club = findClub(clubId);
        club.updateContact(newClubContact);
        return ClubMapper.toDto(club);
    }

    @Transactional
    public ClubDto update(Integer clubId, ClubDto clubDto, String username) {
        Club club = findClub(clubId);

        if (!username.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }
        club.updateName(clubDto.getName());
        club.updateDescription(clubDto.getDescription());
        club.updateContact(clubDto.getContact());
        return ClubMapper.toDto(club);
    }

    @Transactional
    public ClubDto changeOwner(Integer clubId, String updatedName, String username) {
        Club club = findClub(clubId);
        User user = findUser(updatedName);

        if(!username.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }
        club.updateOwner(user);
        return ClubMapper.toDto(club);
    }

    public void deleteClub(Integer clubId, String username) {
        Club club = findClub(clubId);
        if (!username.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }
        clubRepository.delete(club);
    }

    @Transactional
    public ClubDto setImage(Integer clubId, MultipartFile imageFile, String username) {
        Club club = findClub(clubId);

        if (!username.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }
        if (club.getImage() != null) {
            uploadUtil.deleteFile(club.getImage());
        }
        Image image = uploadUtil.buildImage(imageFile);
        Image saved = imageRepository.save(image);

        club.updateImage(saved);
        return ClubMapper.toDto(club);
    }

    private Club findClub(Integer clubId) {
        return clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId)
        );
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
    }
}
