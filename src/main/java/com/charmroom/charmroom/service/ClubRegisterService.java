package com.charmroom.charmroom.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.dto.business.ClubRegisterDto;
import com.charmroom.charmroom.dto.business.ClubRegisterMapper;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.ClubRegister;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRegisterRepository;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubRegisterService {
    private final ClubRegisterRepository clubRegisterRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    public ClubRegisterDto register(String username, Integer clubId) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));

        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        ClubRegister clubRegister = ClubRegister.builder()
                .club(club)
                .user(user)
                .build();
        ClubRegister saved = clubRegisterRepository.save(clubRegister);
        return ClubRegisterMapper.toDto(saved);
    }

    public Page<ClubRegisterDto> getClubRegistersByClub(Integer clubId, Pageable pageable, String username) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        if (!username.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }

        Page<ClubRegister> clubRegisters = clubRegisterRepository.findAllByClub(club, pageable);
        return clubRegisters.map(ClubRegisterMapper::toDto);
    }

    public void deleteClubRegister(String username, Integer clubId, String ownerName) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));

        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        if (!ownerName.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }

        Optional<ClubRegister> found = clubRegisterRepository.findByUserAndClub(user, club);

        if (found.isPresent()) {
            ClubRegister register = found.get();
            clubRegisterRepository.delete(register);
        } else {
            throw new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUBREGISTER
            );
        }
    }

    @Transactional
    public void approveClubRegister(String ownerName, String username, Integer clubId) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));

        Club club = clubRepository.findById(clubId).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "clubId: " + clubId));

        ClubRegister register = clubRegisterRepository.findByUserAndClub(user, club).orElseThrow(() ->
                new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUBREGISTER));

        if(!ownerName.equals(club.getOwner().getUsername())) {
            throw new BusinessLogicException(BusinessLogicError.UNAUTHORIZED_CLUB);
        }

        user.updateClub(register.getClub());
        clubRegisterRepository.delete(register);
    }
}
