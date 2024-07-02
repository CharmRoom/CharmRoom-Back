package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.enums.BoardType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	@Enumerated(EnumType.STRING)
	private BoardType type;
	
	@Column(nullable = false)
	@Builder.Default
	private boolean exposed = false;
	
	public void updateType(BoardType type) {
		this.type = type;
	}
	
	public void updateExposed(Boolean exposed) {
		this.exposed = exposed;
	}
}
