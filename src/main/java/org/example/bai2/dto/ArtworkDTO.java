package org.example.bai2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bai2.entity.Artwork;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkDTO {

    private Long id;
    private String title;
    private String description;
    private boolean isPublished;
    private Long ownerId;
    private String ownerUsername;

    public static ArtworkDTO fromEntity(Artwork artwork) {
        return ArtworkDTO.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .isPublished(artwork.isPublished())
                .ownerId(artwork.getOwner().getId())
                .ownerUsername(artwork.getOwner().getUsername())
                .build();
    }
}
