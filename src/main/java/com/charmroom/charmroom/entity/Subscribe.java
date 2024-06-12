package com.charmroom.charmroom.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;
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
public class Subscribe {
	@EmbeddedId
	private SubscribeId id;
	
	@MapsId("subscriber")
	@OneToOne
	private User subscriber;
	
	@MapsId("target")
	@OneToOne
	private User target;
}
