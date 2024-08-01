package com.charmroom.charmroom.dto.business;

import com.charmroom.charmroom.dto.presentation.ClubDto.ClubResponseDto;
import com.charmroom.charmroom.entity.Club;

import io.jsonwebtoken.lang.Arrays;

public class ClubMapper {
	public static ClubDto toDto(Club entity, String... ignore) {
		if (entity == null) return null;
		var ignores = Arrays.asList(ignore);
		ClubDto dto =  ClubDto.builder()
				.id(entity.getId())
				.name(entity.getName())
				.description(entity.getDescription())
				.contact(entity.getContact())
				.image(ImageMapper.toDto(entity.getImage()))
				.build();

		if (entity.getOwner() != null && !ignores.contains("owner")) {
			dto.setOwner(UserMapper.toDto(entity.getOwner(), "club"));
		}


		if (entity.getUserList().size() > 0 && !ignores.contains("userList")) {
			var userDtoList = entity.getUserList().stream().map(user -> UserMapper.toDto(user, "club")).toList();
			dto.setUserList(userDtoList);
		}
		return dto;
	}

	public static ClubResponseDto toResponse(ClubDto dto) {
		return ClubResponseDto.builder()
				.id(dto.getId())
				.name(dto.getName())
				.description(dto.getDescription())
				.contact(dto.getContact())
				.owner(UserMapper.toResponse(dto.getOwner()))
				.image(ImageMapper.toResponse(dto.getImage()))
				.build();
	}
}
