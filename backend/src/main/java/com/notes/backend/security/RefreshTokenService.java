package com.notes.backend.security;

import com.notes.backend.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Manages opaque refresh tokens. The raw token (high-entropy random value) is
 * returned to the client only once; only its SHA-256 hash is persisted, so a
 * database leak does not expose usable tokens. No raw token is ever logged.
 */
@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64 = Base64.getUrlEncoder().withoutPadding();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Creates and persists a new refresh token for the user and returns the raw
     * token value (the only time it is available in clear text).
     */
    @Transactional
    public String issue(User user) {
        byte[] randomBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);
        String rawToken = base64.encodeToString(randomBytes);

        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs());
        refreshTokenRepository.save(new RefreshToken(user, hash(rawToken), expiresAt));
        return rawToken;
    }

    /**
     * Resolves a raw refresh token to its active, non-expired persisted record.
     *
     * @throws InvalidRefreshTokenException if the token is unknown, revoked or expired.
     */
    @Transactional(readOnly = true)
    public RefreshToken verifyActive(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(InvalidRefreshTokenException::new);
        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException();
        }
        return token;
    }

    /**
     * Revokes the given raw token if it exists and belongs to the given user.
     * Silently ignores unknown tokens so callers cannot probe for validity.
     */
    @Transactional
    public void revoke(String rawToken, User owner) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .filter(token -> token.getUser().getId().equals(owner.getId()))
                .filter(token -> !token.isRevoked())
                .ifPresent(token -> token.setRevokedAt(Instant.now()));
    }

    /** Revokes all active refresh tokens belonging to the user. */
    @Transactional
    public int revokeAll(User user) {
        return refreshTokenRepository.revokeAllActiveByUser(user, Instant.now());
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
