package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final ImageRepository imageRepository;
	private final ClubRepository clubRepository;
	
	private final PasswordEncoder passwordEncoder;
	private final CharmroomUtil.Upload uploadUtil;
	
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
	
	public Page<User> getAllUsersByPageable(Pageable pageable){
		return userRepository.findAll(pageable);
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
	
	public String loadUsernameByEmail(String email) {
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
	
	@Transactional
	public User setClub(String username, Integer clubId) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Club club = clubRepository.findById(clubId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "id: " + clubId));
		user.updateClub(club);
		return user;
	}
	
	@Transactional
	public User setImage(String username, MultipartFile imageFile) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Image image = uploadUtil.buildImage(imageFile);
		Image saved = imageRepository.save(image);
		user.updateImage(saved);
		return user;
	}
}
