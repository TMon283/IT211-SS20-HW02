package org.example.bai2.repository;

import org.example.bai2.entity.TokenSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenSessionRepository extends JpaRepository<TokenSession, Long> {

    Optional<TokenSession> findByRefreshTokenValue(String refreshTokenValue);

    // Fetch all active (non-revoked, non-expired) sessions for an account
    List<TokenSession> findByAccount_IdAndIsRevokedFalseAndIsExpiredFalse(Long accountId);
}
