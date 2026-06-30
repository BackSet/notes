package com.notes.backend.bootstrap;

import com.notes.backend.security.BasePermission;
import com.notes.backend.security.PermissionRepository;
import com.notes.backend.security.Role;
import com.notes.backend.security.RoleName;
import com.notes.backend.security.RoleRepository;
import com.notes.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the always-on RBAC seeding with the initial admin disabled (the default):
 * the ADMIN role and all base permissions exist and are linked, and no users are created.
 */
@SpringBootTest
@Transactional
class RbacSeedingIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void seedsAdminRoleWithAllBasePermissionsAndNoUsers() {
        assertThat(permissionRepository.count()).isEqualTo(BasePermission.values().length);

        Role admin = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
        Set<String> granted = admin.getPermissions().stream()
                .map(p -> p.getName())
                .collect(Collectors.toSet());
        Set<String> expected = Arrays.stream(BasePermission.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        assertThat(granted).isEqualTo(expected);

        assertThat(userRepository.count()).isZero();
    }
}
