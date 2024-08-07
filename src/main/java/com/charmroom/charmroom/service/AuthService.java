package com.charmroom.charmroom.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.security.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JWTUtil jwtUtil;
	
	public String reissue(String refreshToken) {
		if (refreshToken == null)
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "refresh token null");
		
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "refresh token expired");
		}
		
		String category = jwtUtil.getCategory(refreshToken);
		if (!category.equals("refresh"))
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "invalid refresh category");
		
		String username = jwtUtil.getUsername(refreshToken);
		String role = jwtUtil.getRole(refreshToken);
		
		String accessToken = jwtUtil.createJwt("access", username, role, 600000L);
		return accessToken;
	}
}
