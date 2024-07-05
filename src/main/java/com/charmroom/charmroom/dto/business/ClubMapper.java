package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.User;

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
		
		if (entity.getUserList().size() > 0 && !ignores.contains("userList")) {
			List<User> userList = entity.getUserList();
			List<UserDto> userDtoList = new ArrayList<>();
			for (User user : userList) {
				UserDto userDto = UserMapper.toDto(user, "club");
				userDtoList.add(userDto);
			}
			dto.setUserList(userDtoList);
		}
		return dto;
	}
}
