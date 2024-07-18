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
    private Integer clubId;
    private Integer userId;
}
