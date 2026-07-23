package com.vinay.futurevault.service;

import com.vinay.futurevault.entity.PasswordResetToken;
import com.vinay.futurevault.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository repository;

    public PasswordResetTokenService(PasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    // Create Reset Token
    public PasswordResetToken createToken(String email) {

        repository.deleteByEmail(email);

        PasswordResetToken token = new PasswordResetToken();

        token.setEmail(email);
        token.setToken(UUID.randomUUID().toString());

        // Token valid for 15 minutes
        token.setExpiryDate(
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        return repository.save(token);
    }

    // Find Token
    @Transactional(readOnly = true)
    public PasswordResetToken findByToken(String token) {

        return repository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid password reset token"));
    }

    // Verify Expiration
    public PasswordResetToken verifyExpiration(PasswordResetToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {

            repository.delete(token);

            throw new RuntimeException("Password reset token has expired");
        }

        return token;
    }

    // Delete by Email
    public void deleteByEmail(String email) {
        repository.deleteByEmail(email);
    }

    // Delete Token
    public void delete(PasswordResetToken token) {
        repository.delete(token);
    }
}