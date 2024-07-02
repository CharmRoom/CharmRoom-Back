package com.charmroom.charmroom.dto.business;

import java.util.List;

import com.charmroom.charmroom.entity.enums.UserLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
	private Integer id;
	private String username;
	private String email;
	private String nickname;
	private String password;
	private boolean withdraw;
	private UserLevel level;
	private ImageDto image;
	private ClubDto club;
	private List<PointDto> pointList;
}
