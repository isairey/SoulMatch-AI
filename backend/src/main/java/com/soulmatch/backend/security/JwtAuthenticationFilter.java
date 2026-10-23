
package com.soulmatch.backend.security;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soulmatch.backend.users.User;
import com.soulmatch.backend.users.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        if (token.isEmpty()
                || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            UUID userId = jwtService.getUserIdFromToken(token);

            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = optionalUser.get();

            // Evita autenticar cuentas desactivadas o bloqueadas.
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = new SecurityUser(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException exception) {
            // Un token inválido no autentica al usuario.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
