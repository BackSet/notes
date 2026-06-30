package com.notes.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT settings bound from {@code app.jwt.*} (backed by the {@code JWT_*} environment
 * variables). The secret is never logged.
 */
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** HMAC-SHA256 signing secret; must be at least 256 bits (32 bytes). */
    private String secret;

    /** Access token lifetime in milliseconds (short-lived). */
    private long accessExpirationMs = 900_000L;

    /** Refresh token lifetime in milliseconds (long-lived). */
    private long refreshExpirationMs = 604_800_000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public void setAccessExpirationMs(long accessExpirationMs) {
        this.accessExpirationMs = accessExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public void setRefreshExpirationMs(long refreshExpirationMs) {
        this.refreshExpirationMs = refreshExpirationMs;
    }
}
