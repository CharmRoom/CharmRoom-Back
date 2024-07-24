package com.charmroom.charmroom.entity.embid;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ClubRegisterId implements Serializable {
    private static final long serialVersionUID = -1978618759109218319L;
	private Integer clubId;
    private Integer userId;
}
