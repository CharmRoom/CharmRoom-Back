package com.charmroom.charmroom.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public User create(String username, String email, String nickname, String password) {
		if (isDuplicatedUsername(username)) throw new BusinessLogicException(BusinessLogicError.DUPLICATED_USERNAME);
		if (isDuplicatedEmail(email)) throw new BusinessLogicException(BusinessLogicError.DUPLICATED_EMAIL);
		if (isDuplicatedNickname(nickname)) throw new BusinessLogicException(BusinessLogicError.DUPLICATED_NICKNAME);
		
		User user = User.builder()
				.username(username)
				.email(email)
				.nickname(nickname)
				.password(passwordEncoder.encode(password))
				.withdraw(false)
				.build();
		
		return userRepository.save(user);
	}
	
	public Boolean isDuplicatedUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	public Boolean isDuplicatedNickname(String nickname) {
		return userRepository.existsByNickname(nickname);
	}
	
	public Boolean isDuplicatedEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public String findUsernameByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "email: " + email));
		return user.getUsername();
	}
	
	@Transactional
	public User changePassword(String username, String password) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		user.updatePassword(passwordEncoder.encode(password));
		return user;
	}
	
	@Transactional
	public User changeNickname(String username, String nickname) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		user.updateNickname(nickname);
		return user;
	}
	
	@Transactional
	public User changeWithdraw(String username, Boolean withdraw) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		user.updateWithdraw(withdraw);
		return user;
	}
	
}
