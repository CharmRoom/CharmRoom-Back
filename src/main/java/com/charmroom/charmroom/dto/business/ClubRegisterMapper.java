package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.ClubRegister;
import com.charmroom.charmroom.dto.presentation.ClubRegisterDto.ClubRegisterResponseDto;

public class ClubRegisterMapper {
    public static ClubRegisterDto toDto(ClubRegister entity) {
        return ClubRegisterDto.builder()
                .club(ClubMapper.toDto(entity.getClub()))
                .user(UserMapper.toDto(entity.getUser()))
                .build();
    }

    public static ClubRegisterResponseDto toResponse(ClubRegisterDto dto) {
        return ClubRegisterResponseDto.builder()
                .club(ClubMapper.toResponse(dto.getClub()))
                .user(UserMapper.toResponse(dto.getUser()))
                .build();
    }

}
