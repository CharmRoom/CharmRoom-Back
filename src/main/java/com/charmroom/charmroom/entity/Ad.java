package com.charmroom.charmroom.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private LocalDateTime start;

    private LocalDateTime end;

    @Column(length = 255)
    private String link;

    @OneToOne
    private Image image;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateStart(LocalDateTime start) {
        this.start = start;
    }

    public void updateEnd(LocalDateTime end) {
        this.end = end;
    }

    public void updateLink(String link) {
        this.link = link;
    }

    public void updateImage(Image image) {
        this.image = image;
    }
}
