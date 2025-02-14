package com.charmroom.charmroom.service;

import org.springframework.stereotype.Service;

import com.charmroom.charmroom.dto.business.TokenDto;
import com.charmroom.charmroom.entity.RefreshToken;
import com.charmroom.charmroom.exception.BusinessLogicError;
import com.charmroom.charmroom.exception.BusinessLogicException;
import com.charmroom.charmroom.repository.RefreshTokenRepository;
import com.charmroom.charmroom.security.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JWTUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	
	private void tokenValidation(String refreshToken) {
		if (refreshToken == null)
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "refresh token null");
		
		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "refresh token expired");
		}
		
		String category = jwtUtil.getCategory(refreshToken);
		if (!category.equals("refresh"))
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "invalid refresh token");
		
		if (!refreshTokenRepository.existsById(refreshToken))
			throw new BusinessLogicException(BusinessLogicError.INVALID_REFRESH, "Roated refresh token");
	}
	
	public TokenDto reissue(String refreshToken) {
		tokenValidation(refreshToken);
		
		String username = jwtUtil.getUsername(refreshToken);
		String role = jwtUtil.getRole(refreshToken);
		
		String newAccessToken = jwtUtil.createJwt("access", username, role, 600000L);
		String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);
		
		refreshTokenRepository.deleteById(refreshToken);
		refreshTokenRepository.save(RefreshToken.builder()
				.token(newRefreshToken)
				.username(username)
				.build());
		
		return TokenDto.builder()
				.access(newAccessToken)
				.refresh(newRefreshToken)
				.build();
	}
	
	public void logout(String refreshToken) {
		tokenValidation(refreshToken);
		refreshTokenRepository.deleteById(refreshToken);
	}
}
