package com.notes.backend.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Drives the security bootstrap on startup: first seeds the RBAC model
 * (roles/permissions), then applies the initial admin per configuration.
 */
@Component
public class SecurityBootstrapRunner implements ApplicationRunner {

    private final RbacSeeder rbacSeeder;
    private final InitialAdminService initialAdminService;

    public SecurityBootstrapRunner(RbacSeeder rbacSeeder, InitialAdminService initialAdminService) {
        this.rbacSeeder = rbacSeeder;
        this.initialAdminService = initialAdminService;
    }

    @Override
    public void run(ApplicationArguments args) {
        rbacSeeder.seed();
        initialAdminService.apply();
    }
}
