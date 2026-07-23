package com.vinay.futurevault.service;

import com.vinay.futurevault.entity.RefreshToken;
import com.vinay.futurevault.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    // Create Refresh Token
    public RefreshToken createRefreshToken(String email) {

        // Delete old refresh token if it exists
        repository.deleteByEmail(email);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setEmail(email);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return repository.save(refreshToken);
    }

    // Find Refresh Token
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {

        return repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh Token not found"));
    }

    // Verify Expiration
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {

            repository.delete(token);

            throw new RuntimeException("Refresh Token expired");
        }

        return token;
    }

    // Delete Refresh Token by Email
    public void deleteByEmail(String email) {
        repository.deleteByEmail(email);
    }
}