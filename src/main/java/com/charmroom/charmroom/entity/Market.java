package com.charmroom.charmroom.entity;

import com.charmroom.charmroom.entity.enums.MarketArticleState;
import jakarta.persistence.*;
import lombok.*;

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
}
