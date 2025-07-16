package com.vicgroup.veterinaria.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.util.List;

import com.vicgroup.veterinaria.repository.UserRepo;
import com.vicgroup.veterinaria.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwt;
    private final UserRepo users;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        log.info(">>> JwtAuthFilter ejecutado para URI: {}", req.getRequestURI());

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                log.info("Token recibido: {}", token);
                Jws<Claims> claims = jwt.parse(token);
                log.info("Token parseado con éxito");

                Long userId = Long.valueOf(claims.getPayload().getSubject());
                log.info("UserID extraído del token: {}", userId);

//                User u = users.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                User u = users.findByIdWithRole(userId).orElseThrow();

                List<GrantedAuthority> auths =
                        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().getName()));

                UsernamePasswordAuthenticationToken at =
                        new UsernamePasswordAuthenticationToken(u, null, auths);

                SecurityContextHolder.getContext().setAuthentication(at);
                log.info("Usuario autenticado: {} con rol {}", u.getUsername(), u.getRole().getName());
            } catch (JwtException ex) {
                log.warn("Token inválido: {}", ex.getMessage());
            } catch (Exception ex) {
                log.error("Error inesperado durante la autenticación: {}", ex.getMessage(), ex);
            }
        } else {
            log.info("No se encontró token en el encabezado Authorization.");
        }

        chain.doFilter(req, res);
    }
}
