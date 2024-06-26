package com.charmroom.charmroom.dto.business;

import java.util.ArrayList;
import java.util.List;

import com.charmroom.charmroom.entity.Point;
import com.charmroom.charmroom.entity.User;

public class UserMapper {
	public static UserDto toDto(User entity) {
		if (entity == null) return null;
		UserDto dto = UserDto.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.email(entity.getEmail())
				.nickname(entity.getNickname())
				.password(entity.getPassword())
				.withdraw(entity.getWithdraw())
				.level(entity.getLevel())
				.build();
		
		if (entity.getImage() != null) {
			dto.setImage(ImageMapper.toDto(entity.getImage()));
		}
		if (entity.getClub() != null) {
			ClubDto club = ClubMapper.toDto(entity.getClub());
			club.setUserList(null);
			dto.setClub(club);
		}
		if (entity.getPointList().size() > 0) {
			List<Point> pointList = entity.getPointList();
			List<PointDto> pointDtoList = new ArrayList<>();
			for(Point point : pointList) {
				PointDto pointDto = PointMapper.toDto(point);
				pointDto.setUser(null);
				pointDtoList.add(pointDto);
			}
			dto.setPointList(pointDtoList);
		}
		return dto;
	}
}
