package com.charmroom.charmroom.entity.embid;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
public class WishId implements Serializable{
    private static final long serialVersionUID = 9234235235252353L;

    private String userId;
    private Integer marketId;
}
