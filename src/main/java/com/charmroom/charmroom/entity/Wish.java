package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.WishId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class Wish {
    @EmbeddedId
    private WishId id;

    @MapsId(value = "userId")
    @ManyToOne
    private User user;

    @MapsId(value = "marketId")
    @ManyToOne
    private Market market;

    public Wish(WishId id, User user, Market market) {
        this.id = new WishId(user.getId(), market.getId());
        this.user = user;
        this.market = market;
    }
}
