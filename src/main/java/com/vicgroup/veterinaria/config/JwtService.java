package com.vicgroup.veterinaria.config;

import com.vicgroup.veterinaria.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${security.jwt.secret:MWYyZDFlMmU2N2RmZTY3MGU4OTViNGEzYzQ3N2Q0MTMwYjU1YzI4ZWUwOTk2YzFk}")
    private String secret;

    @Value("${security.jwt.expiration-minutes:240}")
    private long expMinutes;

    public String generate(User u) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(u.getId().toString())
                .claim("role", u.getRole().getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
                .compact();

        log.info("Token generado para usuario {}: {}", u.getUsername(), token);
        return token;
    }

    public Jws<Claims> parse(String token) {
        log.info("Parseando token JWT...");
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token);
    }
}
