
package com.soulmatch.backend.security;

import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException(
                    "El correo electrónico es obligatorio"
            );
        }

        User user = userRepository.findByEmailIgnoreCase(
                email.trim()
        ).orElseThrow(() -> new UsernameNotFoundException(
                "No se encontró una cuenta con ese correo electrónico"
        ));

        return new SecurityUser(user);
    }
}
