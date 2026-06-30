package com.notes.backend.admin;

import com.notes.backend.admin.dto.UpdateUserRequest;
import com.notes.backend.admin.dto.UserSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public List<UserSummaryResponse> list() {
        return userAdminService.listUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserSummaryResponse get(@PathVariable Long id) {
        return userAdminService.getUser(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserSummaryResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userAdminService.updateUser(id, request);
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('USER_DISABLE')")
    public UserSummaryResponse disable(@PathVariable Long id) {
        return userAdminService.disableUser(id);
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserSummaryResponse enable(@PathVariable Long id) {
        return userAdminService.enableUser(id);
    }
}
