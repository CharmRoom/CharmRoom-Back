package com.charmroom.charmroom.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.charmroom.charmroom.entity.enums.UserLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Builder
@AllArgsConstructor
public class User implements UserDetails {
	private static final long serialVersionUID = -4608717387472959641L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 255, nullable = false, unique = true)
	private String username;
	
	@Column(length = 255, nullable = false, unique = true)
	private String email;
	
	@Column(length = 255, nullable = false)
	private String nickname;
	
	@Column(length = 255, nullable = false)
	private String password;
	
	@Builder.Default
	@Column(nullable = false)
	private boolean withdraw = false;
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private UserLevel level = UserLevel.ROLE_BASIC;
	
	@OneToOne
	private Image image;
	
	@ManyToOne
	private Club club;
	
	@Builder.Default
	@OneToMany(mappedBy = "user")
	private List<Point> pointList = new ArrayList<>();
	
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

	public void updateEmail(String email) {
		this.email = email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.level.getValue()));
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
}
