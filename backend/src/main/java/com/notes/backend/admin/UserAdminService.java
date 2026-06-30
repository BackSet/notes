package com.notes.backend.admin;

import com.notes.backend.admin.dto.UpdateUserRequest;
import com.notes.backend.admin.dto.UserSummaryResponse;
import com.notes.backend.auth.EmailAlreadyUsedException;
import com.notes.backend.auth.UsernameAlreadyUsedException;
import com.notes.backend.security.Role;
import com.notes.backend.security.RoleName;
import com.notes.backend.user.User;
import com.notes.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Administrative user management: listing, lookup, partial update and
 * enable/disable. Guards against disabling the last enabled administrator.
 */
@Service
public class UserAdminService {

    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> listUsers() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUser(Long id) {
        return toSummary(loadUser(id));
    }

    @Transactional
    public UserSummaryResponse updateUser(Long id, UpdateUserRequest request) {
        User user = loadUser(id);

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyUsedException();
            }
            user.setEmail(request.email());
        }
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new UsernameAlreadyUsedException();
            }
            user.setUsername(request.username());
        }
        if (request.enabled() != null) {
            if (!request.enabled() && user.isEnabled()) {
                assertNotLastEnabledAdmin(user);
            }
            user.setEnabled(request.enabled());
        }

        userRepository.save(user);
        return toSummary(user);
    }

    @Transactional
    public UserSummaryResponse disableUser(Long id) {
        User user = loadUser(id);
        if (user.isEnabled()) {
            assertNotLastEnabledAdmin(user);
            user.setEnabled(false);
            userRepository.save(user);
        }
        return toSummary(user);
    }

    @Transactional
    public UserSummaryResponse enableUser(Long id) {
        User user = loadUser(id);
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.save(user);
        }
        return toSummary(user);
    }

    private User loadUser(Long id) {
        return userRepository.findWithRolesById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private void assertNotLastEnabledAdmin(User user) {
        if (isAdmin(user) && userRepository.countByEnabledTrueAndRoles_Name(RoleName.ADMIN) <= 1) {
            throw new LastAdminException();
        }
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(RoleName.ADMIN::equals);
    }

    private UserSummaryResponse toSummary(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .toList();
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
