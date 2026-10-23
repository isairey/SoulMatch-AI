
package com.soulmatch.backend.auth;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;
import com.soulmatch.backend.users.UserRole;
import com.soulmatch.backend.users.UserStatus;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra una cuenta nueva.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta con ese correo electrónico"
            );
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setRole(UserRole.USER.name());
        user.setStatus(UserStatus.ACTIVE.name());
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        return createAuthResponse(savedUser);
    }

    /**
     * Autentica a un usuario existente.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Correo o contraseña incorrectos"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos"
            );
        }

        if (!UserStatus.ACTIVE.name().equals(user.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta no está activa"
            );
        }

        return createAuthResponse(user);
    }

    /**
     * Genera el JWT y prepara la respuesta de autenticación.
     */
    private AuthResponse createAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        Instant expiresAt = jwtService.getExpiration(token);

        return new AuthResponse(
                token,
                expiresAt,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Normaliza el correo para evitar diferencias por mayúsculas
     * o espacios al principio y al final.
     */
    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo electrónico es obligatorio"
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
