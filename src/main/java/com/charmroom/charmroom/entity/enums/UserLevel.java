package com.charmroom.charmroom.entity.enums;

import lombok.Getter;

@Getter
public enum UserLevel {
	BASIC("ROLE_USER"), ADMIN("ROLE_ADMIN");

	UserLevel(String value) {
		this.value = value;
	}

	private String value;
}
