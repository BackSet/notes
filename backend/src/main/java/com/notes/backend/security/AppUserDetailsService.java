package com.notes.backend.security;

import com.notes.backend.user.User;
import com.notes.backend.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticates by email <em>or</em> username. Backs the {@code DaoAuthenticationProvider}
 * used for login; {@code UsernameNotFoundException} is translated to a generic
 * {@code BadCredentialsException} so it never reveals whether an account exists.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findWithRolesByEmailOrUsername(identifier, identifier).stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserPrincipal(user);
    }
}
