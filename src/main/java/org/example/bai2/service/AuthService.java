package org.example.bai2.service;

import lombok.RequiredArgsConstructor;
import org.example.bai2.dto.AuthResponse;
import org.example.bai2.dto.LoginRequest;
import org.example.bai2.dto.RefreshTokenRequest;
import org.example.bai2.entity.Account;
import org.example.bai2.entity.TokenSession;
import org.example.bai2.repository.AccountRepository;
import org.example.bai2.repository.TokenSessionRepository;
import org.example.bai2.security.AccountDetailsService;
import org.example.bai2.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final TokenSessionRepository tokenSessionRepository;
    private final JwtService jwtService;
    private final AccountDetailsService accountDetailsService;

    // ─────────────────────────── LOGIN ───────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate credentials — throws BadCredentialsException on failure
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        UserDetails userDetails = accountDetailsService.loadUserByUsername(account.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Persist the refresh token in DB
        TokenSession session = TokenSession.builder()
                .refreshTokenValue(refreshToken)
                .isRevoked(false)
                .isExpired(false)
                .account(account)
                .build();
        tokenSessionRepository.save(session);

        return AuthResponse.of(accessToken, refreshToken, account.getUsername());
    }

    // ─────────────────────────── REFRESH ───────────────────────────

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Look up the session in DB
        TokenSession session = tokenSessionRepository.findByRefreshTokenValue(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (session.isRevoked() || session.isExpired()) {
            throw new RuntimeException("Refresh token is revoked or expired");
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            session.setExpired(true);
            tokenSessionRepository.save(session);
            throw new RuntimeException("Refresh token has expired");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = accountDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponse.of(newAccessToken, refreshToken, username);
    }

    // ─────────────────────────── LOGOUT ───────────────────────────

    @Transactional
    public void logout(String refreshToken) {
        // Find all active sessions for this account and revoke them using Stream API
        TokenSession session = tokenSessionRepository.findByRefreshTokenValue(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        Long accountId = session.getAccount().getId();

        List<TokenSession> activeSessions =
                tokenSessionRepository.findByAccount_IdAndIsRevokedFalseAndIsExpiredFalse(accountId);

        // Use Stream API to set all active sessions as revoked and expired
        activeSessions.stream()
                .peek(s -> s.setRevoked(true))
                .forEach(s -> s.setExpired(true));

        tokenSessionRepository.saveAll(activeSessions);
    }
}
