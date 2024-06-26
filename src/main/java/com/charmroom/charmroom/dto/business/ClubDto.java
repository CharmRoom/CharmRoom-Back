package com.charmroom.charmroom.dto.business;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ClubDto {
	private Integer id;
	private String name;
	private String description;
	private String contact;
	private ImageDto image;
	private List<UserDto> userList;
}
