package com.charmroom.charmroom.entity.enums;

import lombok.Getter;

@Getter
public enum UserLevel {
	ROLE_BASIC("ROLE_BASIC"), ROLE_ADMIN("ROLE_ADMIN");

	UserLevel(String value) {
		this.value = value;
	}
	
	private String value;
}
