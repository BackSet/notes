package com.notes.backend.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    /**
     * Returns every user matching the given email or username. Used by bootstrap
     * to detect an existing initial admin under either identifier.
     */
    List<User> findByEmailOrUsername(String email, String username);

    /** Loads a user with roles and permissions eagerly (for login/token issuing). */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    List<User> findWithRolesByEmailOrUsername(String email, String username);

    /** Loads a user with roles and permissions eagerly (for {@code /me} and refresh). */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findWithRolesById(Long id);

    /** Lists all users with their roles eagerly (for admin listing). */
    @EntityGraph(attributePaths = {"roles"})
    List<User> findAllByOrderByIdAsc();

    /** Counts enabled users holding the given role (used to protect the last admin). */
    long countByEnabledTrueAndRoles_Name(String roleName);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
