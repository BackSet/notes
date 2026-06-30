package com.notes.backend.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the initial admin bootstrap, bound from {@code app.initial-admin.*}
 * (backed by the {@code INITIAL_ADMIN_*} environment variables).
 *
 * <p>Both flags default to {@code false} so that nothing is created or overwritten
 * unless explicitly requested.
 */
@ConfigurationProperties(prefix = "app.initial-admin")
public class InitialAdminProperties {

    /** When {@code false}, no admin user is created or updated. */
    private boolean enabled = false;

    /** When {@code true}, an existing matching admin is updated; otherwise it is left untouched. */
    private boolean updateExisting = false;

    private String email;

    private String username;

    /** Raw password; encoded before persistence and never logged. */
    private String password;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUpdateExisting() {
        return updateExisting;
    }

    public void setUpdateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
