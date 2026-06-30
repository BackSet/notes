package com.notes.backend.bootstrap;

import com.notes.backend.security.BasePermission;
import com.notes.backend.security.Permission;
import com.notes.backend.security.PermissionRepository;
import com.notes.backend.security.Role;
import com.notes.backend.security.RoleName;
import com.notes.backend.security.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotently seeds the role/permission model: every {@link BasePermission}
 * exists, the {@link RoleName#ADMIN} role exists, and ADMIN holds all base
 * permissions. Runs on every startup regardless of the initial-admin flags.
 */
@Service
public class RbacSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RbacSeeder(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional
    public void seed() {
        Role admin = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(
                        new Role(RoleName.ADMIN, "Full administrative access")));

        for (BasePermission base : BasePermission.values()) {
            Permission permission = permissionRepository.findByName(base.name())
                    .orElseGet(() -> permissionRepository.save(
                            new Permission(base.name(), base.getDescription())));
            admin.addPermission(permission);
        }

        roleRepository.save(admin);
    }
}
