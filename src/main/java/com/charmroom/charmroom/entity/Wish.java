package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.WishId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
public class Wish {
    @EmbeddedId
    private WishId id;

    @MapsId(value = "userId")
    @ManyToOne
    private User user;

    @MapsId(value = "marketId")
    @ManyToOne
    private Market market;


}
