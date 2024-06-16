package com.charmroom.charmroom.entity.enums;

import lombok.Getter;

@Getter
public enum UserLevel {
	BASIC("BASIC"), ADMIN("ADMIN");

	UserLevel(String value) {
		this.value = value;
	}

	private String value;
}
