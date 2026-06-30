package com.notes.backend.bootstrap;

import com.notes.backend.security.Role;
import com.notes.backend.security.RoleName;
import com.notes.backend.security.RoleRepository;
import com.notes.backend.user.User;
import com.notes.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Branch coverage for the initial admin logic without a Spring context.
 */
@ExtendWith(MockitoExtension.class)
class InitialAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Role adminRole = new Role(RoleName.ADMIN, "Full administrative access");

    private InitialAdminProperties properties;
    private InitialAdminService service;

    @BeforeEach
    void setUp() {
        properties = new InitialAdminProperties();
        properties.setEmail("admin@example.com");
        properties.setUsername("admin");
        properties.setPassword("s3cret-password");
        service = new InitialAdminService(properties, userRepository, roleRepository, passwordEncoder);
        lenient().when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
    }

    @Test
    void doesNothingWhenDisabled() {
        properties.setEnabled(false);

        service.apply();

        verify(userRepository, never()).save(any());
    }

    @Test
    void skipsWhenCredentialsMissing() {
        properties.setEnabled(true);
        properties.setPassword("  ");

        service.apply();

        verify(userRepository, never()).save(any());
    }

    @Test
    void createsAdminWithEncodedPasswordWhenAbsent() {
        properties.setEnabled(true);
        when(userRepository.findByEmailOrUsername("admin@example.com", "admin")).thenReturn(List.of());

        service.apply();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("admin@example.com");
        assertThat(saved.getUsername()).isEqualTo("admin");
        assertThat(saved.isEnabled()).isTrue();
        assertThat(saved.getRoles()).contains(adminRole);
        assertThat(saved.getPasswordHash()).isNotEqualTo("s3cret-password");
        assertThat(passwordEncoder.matches("s3cret-password", saved.getPasswordHash())).isTrue();
    }

    @Test
    void leavesExistingAdminUntouchedWhenUpdateDisabled() {
        properties.setEnabled(true);
        properties.setUpdateExisting(false);
        User existing = new User("old@example.com", "admin", "$existing-hash$");
        when(userRepository.findByEmailOrUsername(anyString(), anyString())).thenReturn(List.of(existing));

        service.apply();

        verify(userRepository, never()).save(any());
        assertThat(existing.getEmail()).isEqualTo("old@example.com");
        assertThat(existing.getPasswordHash()).isEqualTo("$existing-hash$");
    }

    @Test
    void updatesExistingAdminWhenUpdateEnabled() {
        properties.setEnabled(true);
        properties.setUpdateExisting(true);
        User existing = new User("old@example.com", "oldname", "$existing-hash$");
        existing.setEnabled(false);
        when(userRepository.findByEmailOrUsername(anyString(), anyString())).thenReturn(List.of(existing));

        service.apply();

        verify(userRepository).save(existing);
        assertThat(existing.getEmail()).isEqualTo("admin@example.com");
        assertThat(existing.getUsername()).isEqualTo("admin");
        assertThat(existing.isEnabled()).isTrue();
        assertThat(existing.getRoles()).contains(adminRole);
        assertThat(passwordEncoder.matches("s3cret-password", existing.getPasswordHash())).isTrue();
    }
}
