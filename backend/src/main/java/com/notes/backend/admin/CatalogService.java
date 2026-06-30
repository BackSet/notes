package com.notes.backend.admin;

import com.notes.backend.admin.dto.PermissionResponse;
import com.notes.backend.admin.dto.RoleResponse;
import com.notes.backend.security.Permission;
import com.notes.backend.security.PermissionRepository;
import com.notes.backend.security.Role;
import com.notes.backend.security.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Read-only access to the roles and permissions catalog. */
@Service
public class CatalogService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public CatalogService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    private RoleResponse toRoleResponse(Role role) {
        List<String> permissions = role.getPermissions().stream()
                .map(Permission::getName)
                .sorted()
                .toList();
        return new RoleResponse(role.getId(), role.getName(), role.getDescription(), permissions);
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return new PermissionResponse(permission.getId(), permission.getName(), permission.getDescription());
    }
}
