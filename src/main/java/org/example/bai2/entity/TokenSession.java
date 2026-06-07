package org.example.bai2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token_value", nullable = false, columnDefinition = "TEXT")
    private String refreshTokenValue;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked = false;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
