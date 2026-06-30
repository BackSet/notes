package com.notes.backend.admin;

import com.notes.backend.admin.dto.PermissionResponse;
import com.notes.backend.admin.dto.RoleResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminCatalogController {

    private final CatalogService catalogService;

    public AdminCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public List<RoleResponse> roles() {
        return catalogService.listRoles();
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public List<PermissionResponse> permissions() {
        return catalogService.listPermissions();
    }
}
