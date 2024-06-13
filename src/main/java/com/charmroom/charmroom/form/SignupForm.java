package com.charmroom.charmroom.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {
	@Size(min = 3, max = 30)
	@NotEmpty(message = "ID는 필수항목입니다.")
	private String id;

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
