package com.charmroom.charmroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.charmroom.charmroom.dto.business.UserDto;
import com.charmroom.charmroom.dto.business.UserMapper;
import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.entity.enums.UserLevel;
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
	
	public UserDto create(UserDto userDto, MultipartFile imageFile) {
		if (isDuplicatedUsername(userDto.getUsername()))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_USERNAME);
		if (isDuplicatedEmail(userDto.getEmail()))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_EMAIL);
		if (isDuplicatedNickname(userDto.getNickname()))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_NICKNAME);
		
		Image userImage = null;
		if (imageFile != null) {
			Image image = uploadUtil.buildImage(imageFile);
			userImage = imageRepository.save(image);
		}
		
		User user = User.builder()
				.username(userDto.getUsername())
				.email(userDto.getEmail())
				.nickname(userDto.getNickname())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.image(userImage)
				.build();
		User saved = userRepository.save(user);
		return UserMapper.toDto(saved);
	}
	
	public UserDto create(UserDto userDto) {
		return create(userDto, null);
	}
	
	public UserDto getUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		return UserMapper.toDto(user);
	}
	
	public Page<UserDto> getAllUsersByPageable(Pageable pageable){
		Page<User> users = userRepository.findAll(pageable);
		return users.map(user -> UserMapper.toDto(user));
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
	public UserDto changePassword(String username, String password) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		user.updatePassword(passwordEncoder.encode(password));
		
		return UserMapper.toDto(user);
	}
	
	@Transactional
	public UserDto changeNickname(String username, String nickname) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		if (isDuplicatedNickname(nickname))
			throw new BusinessLogicException(BusinessLogicError.DUPLICATED_NICKNAME);
		user.updateNickname(nickname);
		return UserMapper.toDto(user);
	}
	
	@Transactional
	public UserDto changeWithdraw(String username, Boolean withdraw) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		user.updateWithdraw(withdraw);
		return UserMapper.toDto(user);
	}
	
	@Transactional
	public UserDto changeLevel(String username, String level) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		user.updateLevel(UserLevel.valueOf(level));
		return UserMapper.toDto(user);
	}
	
	@Transactional
	public UserDto setClub(String username, Integer clubId) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		Club club = clubRepository.findById(clubId)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_CLUB, "id: " + clubId));
		user.updateClub(club);
		return UserMapper.toDto(user);
	}
	
	@Transactional
	public UserDto setImage(String username, MultipartFile imageFile) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessLogicException(BusinessLogicError.NOTFOUND_USER, "username: " + username));
		if (user.getImage() != null) {
			uploadUtil.deleteImageFile(user.getImage());
		}
		Image image = uploadUtil.buildImage(imageFile);
		Image saved = imageRepository.save(image);
		user.updateImage(saved);
		return UserMapper.toDto(user);
	}
}
