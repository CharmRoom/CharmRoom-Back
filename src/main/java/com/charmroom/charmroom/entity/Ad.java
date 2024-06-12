package com.charmroom.charmroom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @CreationTimestamp
    private LocalDateTime start;

    @CreationTimestamp
    private LocalDateTime end;

    @Column(length = 255)
    private String link;

    @OneToOne
    private Image image;
}
