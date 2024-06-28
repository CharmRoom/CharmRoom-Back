package com.charmroom.charmroom.dto.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentLikeDto {
	private UserDto user;
	private CommentDto comment;
	private Boolean type;
}
