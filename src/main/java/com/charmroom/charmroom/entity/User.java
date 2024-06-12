package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.enums.UserLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
	
	@Column(nullable = false)
	private Boolean withdraw;
	
	@Enumerated(EnumType.STRING)
	private UserLevel level;
	
	@OneToOne
	private Image image;
}
