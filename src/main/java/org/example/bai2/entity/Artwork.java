package org.example.bai2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;
}
