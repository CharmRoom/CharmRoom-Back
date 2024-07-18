package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.embid.ClubRegisterId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class ClubRegister {
    @EmbeddedId
    private ClubRegisterId id;

    @MapsId("clubId")
    @ManyToOne
    private Club club;

    @MapsId("userId")
    @ManyToOne
    private User user;

    public ClubRegister(ClubRegisterId id, Club club, User user) {
        this.id = new ClubRegisterId(club.getId(), user.getId());
        this.club = club;
        this.user = user;
    }
}
