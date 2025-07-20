package com.vicgroup.veterinaria.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173")); // your frontend URL
                    config.setAllowedMethods(List.of("*")); // GET, POST, etc.
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🟢 Públicos (login, register, QR, etc.)
//                        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()

                        // 🔒 ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 🔒 VET
                        .requestMatchers("/api/clinic/**", "/api/prediction/**").hasRole("VET")

                        // 🔒 PET_OWNER (si tienes rutas específicas)
                        .requestMatchers("/api/owner/**").hasRole("PET_OWNER")

                        // 🔒 Lo demás requiere login
//                        .anyRequest().authenticated()


                        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()

                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // ✅ Rutas públicas
//                        .requestMatchers(
//                                "/api/auth/**",               // login, register, etc.
////                                "/api/pets/public/**"         // acceso QR público
//                                "/api/public/**"   // ✅ Esto es lo correcto
//                        ).permitAll()
//
//                        // ✅ Todo lo demás requiere autenticación
//                        .anyRequest().authenticated()
//                )
//                // ✅ Filtro JWT personalizado antes del filtro de autenticación
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
