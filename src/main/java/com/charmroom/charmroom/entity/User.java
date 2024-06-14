package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.enums.UserLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
public class User {
	@Id
	@Column(length = 30, nullable = false)
	private String id;
	
	@Column(length = 255, nullable = false)
	private String email;
	
	@Column(length = 30, nullable = false)
	private String nickname;
	
	@Column(length = 255, nullable = false)
	private String password;
	
	@Builder.Default
	@Column(nullable = false)
	private Boolean withdraw = false;
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private UserLevel level = UserLevel.BASIC;
	
	@OneToOne
	private Image image;
	
	@ManyToOne
	private Club club;
	
	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void updatePassword(String encryptedPassword) {
		this.password = encryptedPassword;
	}
	
	public void updateWithdraw(Boolean withdraw) {
		this.withdraw = withdraw;
	}
	
	public void updateLevel(UserLevel level) {
		this.level = level;
	}
	
	public void updateImage(Image image) {
		this.image = image;
	}
	
	public void updateClub(Club club) {
		this.club = club;
	}
}
