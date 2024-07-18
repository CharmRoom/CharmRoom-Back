package com.charmroom.charmroom.dto.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ClubRegisterDto {
    private Integer id;
    private UserDto user;
    private ClubDto club;
}
