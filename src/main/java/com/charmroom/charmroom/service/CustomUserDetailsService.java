package com.charmroom.charmroom.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.dto.CustomUserDetails;
import com.charmroom.charmroom.entity.User;
import com.charmroom.charmroom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var userByUsername = userRepository.findByUsername(username);
		if (userByUsername.isEmpty()) {
			throw new UsernameNotFoundException("Username Not Found: " + username);
		}
		User user = userByUsername.get();
		return new CustomUserDetails(userByUsername.get());
	}
}
