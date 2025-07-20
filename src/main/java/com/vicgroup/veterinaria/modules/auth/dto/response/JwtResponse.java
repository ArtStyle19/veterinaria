package com.vicgroup.veterinaria.modules.auth.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Instant expiresAt;
}