package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.charmroom.charmroom.entity.Club;
import com.charmroom.charmroom.entity.Image;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.ClubRepository;
import com.charmroom.charmroom.repository.ImageRepository;
import com.charmroom.charmroom.repository.UserRepository;
import com.charmroom.charmroom.util.CharmroomUtil;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private ImageRepository imageRepository;
	@Mock
	private ClubRepository clubRepository;
	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@Mock
	private CharmroomUtil.Upload uploadUtil;
	@InjectMocks
	private UserService userService;
	
	private User mockedUser;
	private String username;
	private String nickname;
	private String email;
	private String password;
	private String encryptedPassword;
	
	User buildUser(String prefix) {
		return User.builder()
				.username(prefix + username)
				.nickname(prefix + nickname)
				.email(email)
				.password(encryptedPassword)
				.build();
	}
	@BeforeEach
	void setup() {
		username = "username";
		nickname = "nickname";
		email = "email@example.com";
		password = "password";
		encryptedPassword = passwordEncoder.encode(password);
		mockedUser = buildUser("");
	}
	
	@Nested
	class Create {
		@Test
		void success(){
			// given
			doReturn(mockedUser).when(userRepository).save(any(User.class));
			doReturn(false).when(userRepository).existsByUsername(username);
			doReturn(false).when(userRepository).existsByEmail(email);
			doReturn(false).when(userRepository).existsByNickname(nickname);
			
			// when
			User created = userService.create(username, email, nickname, password);
			// then
			assertThat(created).isNotNull();
			assertThat(created.getPassword()).isEqualTo(mockedUser.getPassword());
		}
		
		@Test
		void failUsernameDuplicated() {
			// given
			doReturn(true).when(userRepository).existsByUsername(username);
			
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				// when
				userService.create(username, email, nickname, password);	
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.DUPLICATED_USERNAME);
		}
		
		@Test
		void failEmailDuplicated() {
			// given
			doReturn(false).when(userRepository).existsByUsername(username);
			doReturn(true).when(userRepository).existsByEmail(email);
			
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				// when
				userService.create(username, email, nickname, password);	
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.DUPLICATED_EMAIL);
			
		}
		
		@Test
		void failNicknameDuplicated() {
			// given
			doReturn(false).when(userRepository).existsByUsername(username);
			doReturn(false).when(userRepository).existsByEmail(email);
			doReturn(true).when(userRepository).existsByNickname(nickname);
			
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				// when
				userService.create(username, email, nickname, password);	
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.DUPLICATED_NICKNAME);
		}
	}
	
	@Nested
	class GetUserList{
		@Test
		void success() {
			// given
			var listUser = List.of(buildUser("1"), buildUser("2"), buildUser("3"));
			var pageRequest = PageRequest.of(0, 3, Direction.ASC, "username");
			var pageUser = new PageImpl<>(listUser);
			
			doReturn(pageUser).when(userRepository).findAll(pageRequest);
			
			// when
			var result = userService.getAllUsersByPageable(pageRequest);
			
			assertThat(result).hasSize(3);
		}
	}
	
	@Nested
	class FindUsernameByEmail {
		@Test
		void success() {
			// given
			doReturn(Optional.of(mockedUser)).when(userRepository).findByEmail(email);
			
			// when
			String foundUsername = userService.loadUsernameByEmail(email);
			
			// then
			assertThat(foundUsername).isEqualTo(username);
		}
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByEmail(email);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.loadUsernameByEmail(email);
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("email: "+ email);
		}
	}
	
	@Nested
	class ChangePassword{
		@Test
		void success() {
			// given
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			
			// when
			User changed = userService.changePassword(username, "1234");
			
			// then
			assertThat(passwordEncoder.matches(password, changed.getPassword())).isFalse();
			assertThat(passwordEncoder.matches("1234", changed.getPassword())).isTrue();
		}
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(username);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.changePassword(username, "1234");
			});
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + username);
		}
	}
	
	@Nested
	class ChangeNickname{
		@Test
		void success() {
			// given
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			
			// when
			User changed = userService.changeNickname(username, "1234");
			
			// then
			assertThat(changed.getNickname()).isNotEqualTo(nickname);
			assertThat(changed.getNickname()).isEqualTo("1234");
		}
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(username);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.changeNickname(username, "1234");
			});
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + username);
		}
	}
	
	@Nested
	class ChangeWithdraw{
		@Test
		void success() {
			// given
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			
			// when
			User changed = userService.changeWithdraw(username, true);
			
			// then
			assertThat(changed.getWithdraw()).isTrue();
		}
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(username);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.changeWithdraw(username, true);
			});
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + username);
		}
	}
	
	@Nested
	class SetClub{
		@Test
		void success() {
			// given
			Club club = Club.builder()
					.id(1)
					.name("")
					.description("")
					.contact("")
					.build();
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			doReturn(Optional.of(club)).when(clubRepository).findById(1);
			
			// when
			User changed = userService.setClub(username, 1);
			
			// then
			assertThat(changed.getClub()).isEqualTo(club);
		}
		@Test
		void failByNotfoundUser() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(username);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.setClub(username, 1);
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + username);
		}
		@Test
		void failByNotfoundClub() {
			// given
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			doReturn(Optional.empty()).when(clubRepository).findById(1);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.setClub(username, 1);
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_CLUB);
			assertThat(thrown.getMessage()).isEqualTo("id: 1");
		}
	}
	
	@Nested
	class SetImage{
		@Test
		void success() {
			// given
			MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
			Image image = Image.builder()
					.path("")
					.originalName("")
					.build(); 
			
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			doReturn(image).when(uploadUtil).buildImage(imageFile);
			doReturn(image).when(imageRepository).save(image);
			
			
			// when
			User changed = userService.setImage(username, imageFile);
			
			// then
			assertThat(changed.getImage()).isEqualTo(image);
		}
		@Test
		void failByNotFoundUser() {
			MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "image/png", "test".getBytes());
			doReturn(Optional.empty()).when(userRepository).findByUsername(username);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.setImage(username, imageFile);
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.NOTFOUND_USER);
			assertThat(thrown.getMessage()).isEqualTo("username: " + username);
		}
		
		@Test
		void failByNotImageFile() {
			// given
			MockMultipartFile imageFile = new MockMultipartFile("file", "test.png", "video/png", "test".getBytes());
			doReturn(Optional.of(mockedUser)).when(userRepository).findByUsername(username);
			doThrow(new BusinessLogicException(BusinessLogicError.FILE_NOT_IMAGE))
				.when(uploadUtil).buildImage(imageFile);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () ->{
				userService.setImage(username, imageFile);
			});
			
			// then
			assertThat(thrown.getError()).isEqualTo(BusinessLogicError.FILE_NOT_IMAGE);
		}
	}
}
