package com.notes.backend.bootstrap;

import com.notes.backend.security.Role;
import com.notes.backend.security.RoleName;
import com.notes.backend.security.RoleRepository;
import com.notes.backend.user.User;
import com.notes.backend.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Creates or updates the initial administrator according to the configured flags.
 * Passwords are always encoded before persistence and never written to the log.
 */
@Service
public class InitialAdminService {

    private static final Logger log = LoggerFactory.getLogger(InitialAdminService.class);

    private final InitialAdminProperties properties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialAdminService(InitialAdminProperties properties,
                               UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void apply() {
        if (!properties.isEnabled()) {
            log.info("Initial admin bootstrap disabled (app.initial-admin.enabled=false); skipping.");
            return;
        }

        if (!hasRequiredCredentials()) {
            log.warn("Initial admin bootstrap enabled but email, username or password is missing; skipping.");
            return;
        }

        String email = properties.getEmail();
        String username = properties.getUsername();

        List<User> matches = userRepository.findByEmailOrUsername(email, username)
                .stream()
                .distinct()
                .toList();

        if (matches.size() > 1) {
            log.warn("Initial admin matches multiple existing users by email/username; "
                    + "skipping to avoid an ambiguous update.");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "ADMIN role not found; RBAC seeding must run before initial admin bootstrap."));

        if (matches.isEmpty()) {
            createAdmin(email, username, adminRole);
        } else {
            updateAdmin(matches.get(0), email, username, adminRole);
        }
    }

    private void createAdmin(String email, String username, Role adminRole) {
        User admin = new User(email, username, passwordEncoder.encode(properties.getPassword()));
        admin.setEnabled(true);
        admin.addRole(adminRole);
        userRepository.save(admin);
        log.info("Created initial admin user '{}'.", username);
    }

    private void updateAdmin(User admin, String email, String username, Role adminRole) {
        if (!properties.isUpdateExisting()) {
            log.info("Initial admin '{}' already exists and update is disabled "
                    + "(app.initial-admin.update-existing=false); leaving it untouched.", admin.getUsername());
            return;
        }

        admin.setEmail(email);
        admin.setUsername(username);
        admin.setEnabled(true);
        admin.setPasswordHash(passwordEncoder.encode(properties.getPassword()));
        admin.addRole(adminRole);
        userRepository.save(admin);
        log.info("Updated initial admin user '{}'.", username);
    }

    private boolean hasRequiredCredentials() {
        return StringUtils.hasText(properties.getEmail())
                && StringUtils.hasText(properties.getUsername())
                && StringUtils.hasText(properties.getPassword());
    }
}
