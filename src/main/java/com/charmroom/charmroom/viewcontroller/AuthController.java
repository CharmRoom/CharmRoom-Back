package com.charmroom.charmroom.viewcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.charmroom.charmroom.dto.SignupDto.SignupRequestDto;
import com.charmroom.charmroom.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
	private final UserService userService;

	@GetMapping("/signup")
	public String signup(SignupRequestDto signupRequestDto) {
		return "signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		if (!signupRequestDto.getPassword().equals(signupRequestDto.getRePassword())) {
			bindingResult.rejectValue("rePassword", "passwordIncorrect",
					"패스워드 확인이 일치하지 않습니다.");
			return "signup_form";
		}

		userService.create(signupRequestDto.getUsername(), signupRequestDto.getEmail(),
				signupRequestDto.getNickname(), signupRequestDto.getPassword());

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}

}
