package com.charmroom.charmroom.entity.embid;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SubscribeId implements Serializable{

	private static final long serialVersionUID = 20249614918670233L;
	
	private Integer subscriber;
	private Integer target;
}
