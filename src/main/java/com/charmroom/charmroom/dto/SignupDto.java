package com.charmroom.charmroom.dto;

import com.charmroom.charmroom.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SignupDto {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SignupRequestDto {
		@Size(min = 3, max = 30)
		@NotEmpty(message = "ID는 필수항목입니다.")
		private String username;
	
		@Size(max = 255)
		@NotEmpty(message = "비밀번호는 필수항목입니다.")
		private String password;
		
		@Size(max = 255)
		@NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
		private String rePassword;
		
		@Size(max = 255)
		@NotEmpty(message = "이메일은 필수항목입니다.")
		@Email
		private String email;
		
		@Size(min = 3, max = 30)
		@NotEmpty(message = "닉네임은 필수항목입니다.")
		private String nickname;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SignupResponseDto {
		private String username;
		private String email;
		private String nickname;
		
		public static SignupResponseDto fromEntity(User user) {
			return SignupResponseDto.builder()
					.username(user.getUsername())
					.email(user.getEmail())
					.nickname(user.getNickname())
					.build();
		}
	}
	
	
}
