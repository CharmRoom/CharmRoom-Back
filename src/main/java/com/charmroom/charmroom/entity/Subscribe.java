package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.SubscribeId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class Subscribe {
	@EmbeddedId
	private SubscribeId id;
	
	@MapsId("subscriber")
	@OneToOne
	private User subscriber;
	
	@MapsId("target")
	@OneToOne
	private User target;

	public Subscribe(SubscribeId id, User subscriber, User target) {
		this.id = new SubscribeId(subscriber.getId(), target.getId());
		this.subscriber = subscriber;
		this.target = target;
	}
}
