package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.enums.MarketArticleState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
@AllArgsConstructor
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Article article;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private MarketArticleState state;

    @Column(nullable = false)
    private String tag;

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateTag(String tag) {
        this.tag = tag;
    }

    public void updateState(MarketArticleState enumType) {
        this.state = enumType;
    }
}
