package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.entity.ClubRegister;

public class ClubRegisterMapper {
    public static ClubRegisterDto toDto(ClubRegister entity) {
        return ClubRegisterDto.builder()
                .club(ClubMapper.toDto(entity.getClub()))
                .user(UserMapper.toDto(entity.getUser()))
                .build();
    }

}
