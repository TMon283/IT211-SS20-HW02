package org.example.bai2.controller;

import lombok.RequiredArgsConstructor;
import org.example.bai2.dto.ArtworkDTO;
import org.example.bai2.service.ArtworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;

    /**
     * GET /api/gallery/artworks
     *
     * Requires a valid JWT (any authenticated role).
     * - ROLE_ADMIN  → all artworks
     * - ROLE_ARTIST → published artworks + own artworks
     */
    @GetMapping("/artworks")
    public ResponseEntity<List<ArtworkDTO>> getArtworks(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ArtworkDTO> artworks = artworkService.getArtworks(userDetails);
        return ResponseEntity.ok(artworks);
    }
}
