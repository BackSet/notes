package com.notes.backend.bootstrap;

import com.notes.backend.security.RoleName;
import com.notes.backend.user.User;
import com.notes.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the initial admin is created on startup when enabled, with an encoded
 * password and the ADMIN role assigned.
 */
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        // Isolated in-memory DB so the created admin does not leak into the shared
        // testdb used by other contexts (DB_CLOSE_DELAY=-1 keeps it JVM-wide).
        "spring.datasource.url=jdbc:h2:mem:admin-enabled-test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "app.initial-admin.enabled=true",
        "app.initial-admin.email=root@example.com",
        "app.initial-admin.username=root",
        "app.initial-admin.password=integration-test-password"
})
class InitialAdminEnabledIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createsEnabledAdminWithEncodedPasswordAndAdminRole() {
        User admin = userRepository.findByUsername("root").orElseThrow();

        assertThat(admin.getEmail()).isEqualTo("root@example.com");
        assertThat(admin.isEnabled()).isTrue();
        assertThat(admin.getPasswordHash()).isNotEqualTo("integration-test-password");
        assertThat(passwordEncoder.matches("integration-test-password", admin.getPasswordHash())).isTrue();
        assertThat(admin.getRoles())
                .extracting(role -> role.getName())
                .containsExactly(RoleName.ADMIN);
    }
}
