package com.charmroom.charmroom.entity.embid;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class WishId {
    private static final long serialVersionUID = 9234235235252353L;

    private String userId;
    private Integer marketId;
}
