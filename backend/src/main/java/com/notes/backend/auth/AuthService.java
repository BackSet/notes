package com.notes.backend.auth;

import com.notes.backend.auth.dto.LoginRequest;
import com.notes.backend.auth.dto.LogoutRequest;
import com.notes.backend.auth.dto.MeResponse;
import com.notes.backend.auth.dto.RefreshRequest;
import com.notes.backend.auth.dto.RegisterRequest;
import com.notes.backend.auth.dto.TokenResponse;
import com.notes.backend.security.AppUserPrincipal;
import com.notes.backend.security.InvalidRefreshTokenException;
import com.notes.backend.security.JwtService;
import com.notes.backend.security.Permission;
import com.notes.backend.security.RefreshToken;
import com.notes.backend.security.RefreshTokenService;
import com.notes.backend.security.Role;
import com.notes.backend.user.User;
import com.notes.backend.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orchestrates registration, login and the refresh-token lifecycle. Passwords and
 * tokens are never logged; login failures surface a single generic error.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /** Public registration: creates an enabled user with no roles (never ADMIN). */
    @Transactional
    public MeResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException();
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyUsedException();
        }
        User user = new User(request.email(), request.username(), passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        userRepository.save(user);
        return toMeResponse(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password()));
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }

        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        User user = userRepository.findWithRolesById(principal.getUser().getId())
                .orElseThrow(InvalidCredentialsException::new);

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenService.verifyActive(request.refreshToken());
        User user = userRepository.findWithRolesById(token.getUser().getId())
                .orElseThrow(InvalidRefreshTokenException::new);
        if (!user.isEnabled()) {
            throw new InvalidRefreshTokenException();
        }
        // No rotation: the existing refresh token remains valid until it expires or is revoked.
        String accessToken = jwtService.generateAccessToken(user);
        return TokenResponse.bearer(accessToken, request.refreshToken(), jwtService.getAccessExpirationMs());
    }

    @Transactional
    public void logout(Long userId, LogoutRequest request) {
        User user = userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
        refreshTokenService.revoke(request.refreshToken(), user);
    }

    @Transactional
    public void logoutAll(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
        refreshTokenService.revokeAll(user);
    }

    @Transactional(readOnly = true)
    public MeResponse me(Long userId) {
        User user = userRepository.findWithRolesById(userId)
                .orElseThrow(InvalidCredentialsException::new);
        return toMeResponse(user);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.issue(user);
        return TokenResponse.bearer(accessToken, refreshToken, jwtService.getAccessExpirationMs());
    }

    private MeResponse toMeResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .sorted()
                .toList();
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled(), roles, permissions);
    }
}
