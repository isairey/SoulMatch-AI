package com.soulmatch.backend.users;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un usuario nuevo.
     */
    public UserResponse createUser(UserRequest request) {
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
        user.setStatus("ACTIVE");
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    /**
     * Busca un usuario por su identificador.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        return UserResponse.from(findUserById(id));
    }

    /**
     * Obtiene todos los usuarios.
     * Este método debe utilizarse solamente desde endpoints protegidos.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * Busca un usuario por correo electrónico.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        return UserResponse.from(user);
    }

    /**
     * Actualiza el correo electrónico de un usuario.
     */
    public UserResponse updateEmail(UUID id, String newEmail) {
        User user = findUserById(id);
        String normalizedEmail = normalizeEmail(newEmail);

        if (!user.getEmail().equalsIgnoreCase(normalizedEmail)
                && userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta con ese correo electrónico"
            );
        }

        user.setEmail(normalizedEmail);
        user.setEmailVerified(false);

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Cambia el estado de una cuenta.
     */
    public UserResponse updateStatus(UUID id, String status) {
        User user = findUserById(id);

        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!List.of("ACTIVE", "SUSPENDED", "BLOCKED", "PENDING")
                .contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado de usuario no válido"
            );
        }

        user.setStatus(normalizedStatus);

        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Comprueba si existe un usuario.
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(
                normalizeEmail(email)
        );
    }

    /**
     * Elimina una cuenta por UUID.
     * Debe invocarse únicamente desde una operación autorizada.
     */
    public void deleteUser(UUID id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo electrónico es obligatorio"
            );
        }

        return email.trim().toLowerCase();
    }
}