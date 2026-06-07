package org.example.bai2.repository;

import org.example.bai2.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    // Used by ROLE_ADMIN: all artworks returned via findAll()
    // Used by ROLE_ARTIST: filter in service layer using Stream API
}
