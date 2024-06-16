package com.charmroom.charmroom.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public User create(String id, String email, String nickname, String password ) {
		User user = User.builder()
				.username(id)
				.email(email)
				.nickname(nickname)
				.password(passwordEncoder.encode(password))
				.withdraw(false)
				.build();
		userRepository.save(user);
		return user;
	}
}
