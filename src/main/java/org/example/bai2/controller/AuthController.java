package org.example.bai2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bai2.dto.AuthResponse;
import org.example.bai2.dto.LoginRequest;
import org.example.bai2.dto.RefreshTokenRequest;
import org.example.bai2.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gallery/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/gallery/auth/login
     * Returns access token + refresh token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/gallery/auth/refresh
     * Issues a new access token using a valid refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/gallery/auth/logout
     * Revokes the refresh token (is_revoked = true, is_expired = true).
     * Body: { "refreshToken": "..." }
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
