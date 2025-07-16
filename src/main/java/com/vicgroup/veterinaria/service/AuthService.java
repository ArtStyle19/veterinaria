package com.vicgroup.veterinaria.service;

import com.vicgroup.veterinaria.model.*;
import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.security.authentication.BadCredentialsException; // For incorrect password/username
import org.springframework.security.core.userdetails.UsernameNotFoundException; // For user not found in face login
import org.springframework.security.crypto.password.PasswordEncoder; // For password hashing
import org.springframework.stereotype.Service; // Marks this class as a Spring service
import org.springframework.transaction.annotation.Transactional; // For transactional methods

import java.time.Duration; // For calculating JWT expiration time
import java.time.Instant;  // For calculating JWT expiration time

// Import your custom model classes (entities)

// Import your custom repository interfaces
import com.vicgroup.veterinaria.repository.RoleRepo;
import com.vicgroup.veterinaria.repository.UserRepo;
import com.vicgroup.veterinaria.repository.ClinicRepo;
import com.vicgroup.veterinaria.repository.VetProfileRepo;
import com.vicgroup.veterinaria.repository.OwnerProfileRepo;

// Import your custom request/response DTOs
import com.vicgroup.veterinaria.dto.RegisterAdminRequest;
import com.vicgroup.veterinaria.dto.RegisterOwnerRequest;
import com.vicgroup.veterinaria.dto.RegisterVetRequest;
import com.vicgroup.veterinaria.dto.LoginRequest;
import com.vicgroup.veterinaria.dto.FaceLoginRequest;
import com.vicgroup.veterinaria.dto.JwtResponse; // Your custom JWT response DTO

// Import your custom services
import com.vicgroup.veterinaria.config.security.JwtService;


@Service @Transactional
@RequiredArgsConstructor
public class AuthService {

    private final RoleRepo roles;
    private final UserRepo users;
    private final ClinicRepo clinics;
    private final VetProfileRepo vets;
    private final OwnerProfileRepo owners;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final FaceVerifyClient face;

    public JwtResponse registerAdmin(RegisterAdminRequest r) {
        return issue(saveNewUser(r.getUsername(), r.getPassword(), "ADMIN"));
    }



    public JwtResponse registerOwner(RegisterOwnerRequest r) {
        User u = saveNewUser(r.getUsername(), r.getPassword(), "PET_OWNER");
        OwnerProfile opr = new OwnerProfile();
        opr.setUser(u);
        opr.setEmail(r.getEmail());
        opr.setCelNum(r.getCelNum());
        owners.save(opr);
        return issue(u);
    }

    public JwtResponse registerVet(RegisterVetRequest r) {
        User u = saveNewUser(r.getUsername(), r.getPassword(), "VET");
        Clinic c = clinics.findById(r.getClinicId()).orElseThrow();
        VetProfile vp = new VetProfile();
        vp.setUser(u); vp.setClinic(c);
        vp.setCelNum(r.getCelNum());
        vp.setEmail(r.getEmail());
        vets.save(vp);
        return issue(u);
    }

    public JwtResponse login(LoginRequest r) {
        User u = users.findByUsername(r.getUsername())
                .orElseThrow(() -> new BadCredentialsException("user"));
        if(!encoder.matches(r.getPassword(), u.getPasswordHash()))
            throw new BadCredentialsException("pass");
        return issue(u);
    }

    public JwtResponse faceLogin(FaceLoginRequest r) {
        VetProfile vp = vets.findById(r.getVetId())
                .orElseThrow(() -> new UsernameNotFoundException("vet"));
        if(!face.verify(r.getImageBase64(), vp.getFaceEmbedding()))
            throw new BadCredentialsException("face");
        return issue(vp.getUser());
    }

    /* ---------- helpers ---------- */
    private User saveNewUser(String username, String rawPass, String roleName) {
        Role role = roles.findByName(roleName).orElseThrow();
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(rawPass));
        u.setRole(role);
        return users.save(u);
    }
    private JwtResponse issue(User u) {
        String token = jwt.generate(u);
        Instant exp  = Instant.now().plus(
                Duration.ofMinutes(240)); // mirror application.yaml
        return new JwtResponse(token, exp);
    }
}
