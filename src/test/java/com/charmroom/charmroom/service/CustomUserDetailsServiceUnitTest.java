package com.charmroom.charmroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;
	
	private User user;
	private String username;
	
	@BeforeEach
	void setup() {
		username = "username";
		user = User.builder()
				.username(username)
				.nickname(username)
				.email(username + "@test.com")
				.password("1234")
				.build();
	}
	
	@Nested
	class LoadUserByUsername {
		@Test
		void success() {
			// given
			doReturn(Optional.of(user)).when(userRepository).findByUsername(username);
			
			// when
			var customUserDetails = customUserDetailsService.loadUserByUsername(username);
			
			// then
			assertThat(customUserDetails).isNotNull();
			assertThat(customUserDetails.getUsername()).isEqualTo(username);
		}
		
		@Test
		void fail() {
			// given
			doReturn(Optional.empty()).when(userRepository).findByUsername(username);
			
			// when
			var thrown = assertThrows(UsernameNotFoundException.class, () -> {
				customUserDetailsService.loadUserByUsername(username);
			});
			
			// then
			assertThat(thrown.getMessage()).isEqualTo("Username Not Found: " + username);
		}
	}
}
