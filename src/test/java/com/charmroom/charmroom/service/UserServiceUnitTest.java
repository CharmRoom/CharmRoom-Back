package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@InjectMocks
	private UserService userService;
	
	private User mockedUser;
	private String username;
	private String nickname;
	private String email;
	private String password;
	private String encryptedPassword;
	
	@BeforeEach
	void setup() {
		username = "username";
		nickname = "nickname";
		email = "email@example.com";
		password = "password";
		encryptedPassword = passwordEncoder.encode(password);
		mockedUser = User.builder()
				.username(username)
				.nickname(nickname)
				.email(email)
				.password(encryptedPassword)
				.build();
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
			assertThat(created.getPassword()).isEqualTo(encryptedPassword);
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
	class FindUsernameByEmail {
		@Test
		void success() {
			// given
			doReturn(Optional.of(mockedUser)).when(userRepository).findByEmail(email);
			
			// when
			String foundUsername = userService.findUsernameByEmail(email);
			
			// then
			assertThat(foundUsername).isEqualTo(username);
		}
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByEmail(email);
			
			// when
			var thrown = assertThrows(BusinessLogicException.class, () -> {
				userService.findUsernameByEmail(email);
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
}
