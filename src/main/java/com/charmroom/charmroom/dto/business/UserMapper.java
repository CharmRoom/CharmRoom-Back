package com.charmroom.charmroom.dto.business;

import java.util.List;

import com.charmroom.charmroom.dto.presentation.UserDto.CreateUserRequestDto;
import com.charmroom.charmroom.dto.presentation.UserDto.UserResponseDto;
import com.charmroom.charmroom.entity.User;

import io.jsonwebtoken.lang.Arrays;

public class UserMapper {
	public static UserDto toDto(User entity, String... ignore) {
		if (entity == null) return null;
		UserDto dto = UserDto.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.email(entity.getEmail())
				.nickname(entity.getNickname())
				.password(entity.getPassword())
				.withdraw(entity.isWithdraw())
				.level(entity.getLevel())
				.build();
		List<String> ignores = Arrays.asList(ignore);
		if (entity.getImage() != null && !ignores.contains("image")) {
			dto.setImage(ImageMapper.toDto(entity.getImage()));
		}
		if (entity.getClub() != null && !ignores.contains("club")) {
			ClubDto club = ClubMapper.toDto(entity.getClub(), "userList");
			club.setUserList(null);
			dto.setClub(club);
		}
		if (entity.getPointList().size() > 0 && !ignores.contains("pointList")) {
			var pointList = entity.getPointList().stream().map(point -> PointMapper.toDto(point, "user")).toList();
			dto.setPointList(pointList);
		}
		return dto;
	}
	
	public static UserResponseDto toResponse(UserDto dto) {
		return UserResponseDto.builder()
				.username(dto.getUsername())
				.email(dto.getEmail())
				.nickname(dto.getNickname())
				.withdraw(dto.isWithdraw())
				.level(dto.getLevel().getValue())
				.image(ImageMapper.toResponse(dto.getImage()))
				.build();
	}
	
	public static UserDto toBusinessDto(CreateUserRequestDto createUserRequestDto) {
		return UserDto.builder()
				.username(createUserRequestDto.getUsername())
				.email(createUserRequestDto.getEmail())
				.nickname(createUserRequestDto.getNickname())
				.password(createUserRequestDto.getPassword())
				.build();
	}
}
