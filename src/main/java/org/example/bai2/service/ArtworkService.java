package org.example.bai2.service;

import lombok.RequiredArgsConstructor;
import org.example.bai2.dto.ArtworkDTO;
import org.example.bai2.entity.Account;
import org.example.bai2.repository.AccountRepository;
import org.example.bai2.repository.ArtworkRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final AccountRepository accountRepository;

    /**
     * Returns artworks based on the caller's role.
     *
     * ROLE_ADMIN  → all artworks (including unpublished)
     * ROLE_ARTIST → published artworks OR owned by the current user
     */
    public List<ArtworkDTO> getArtworks(UserDetails userDetails) {
        // Check roles using Stream API + anyMatch
        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (isAdmin) {
            // ADMIN: return all artworks mapped to DTOs via Stream API
            return artworkRepository.findAll().stream()
                    .map(ArtworkDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        // ARTIST: get current user's account id
        Account account = accountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Long currentUserId = account.getId();

        // Return published artworks OR artworks owned by this user
        return artworkRepository.findAll().stream()
                .filter(artwork -> artwork.isPublished()
                        || artwork.getOwner().getId().equals(currentUserId))
                .map(ArtworkDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
