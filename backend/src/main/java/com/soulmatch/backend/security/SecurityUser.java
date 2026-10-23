
package com.soulmatch.backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.soulmatch.backend.users.User;

public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getId() {
        return user.getId().toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole();

        if (role == null || role.isBlank()) {
            return List.of();
        }

        String normalizedRole = role.startsWith("ROLE_")
                ? role.substring(5)
                : role;

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + normalizedRole)
        );
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        String status = user.getStatus();

        return status != null
                && !status.equalsIgnoreCase("BLOCKED")
                && !status.equalsIgnoreCase("SUSPENDED");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(user.getStatus());
    }
}
