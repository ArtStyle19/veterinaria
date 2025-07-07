package com.vicgroup.veterinaria.controller;

import lombok.RequiredArgsConstructor; // For automatic constructor injection
import org.springframework.web.bind.annotation.PostMapping; // For handling HTTP POST requests
import org.springframework.web.bind.annotation.RequestBody; // For mapping HTTP request body to method parameters
import org.springframework.web.bind.annotation.RequestMapping; // For mapping web requests onto specific handler classes/methods
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller

import jakarta.validation.Valid; // For enabling validation on request body objects (requires Spring Boot Validation dependency)

// Import your custom service
import com.vicgroup.veterinaria.service.AuthService;

// Import your custom request DTOs
import com.vicgroup.veterinaria.dto.RegisterAdminRequest;
import com.vicgroup.veterinaria.dto.RegisterOwnerRequest;
import com.vicgroup.veterinaria.dto.RegisterVetRequest;
import com.vicgroup.veterinaria.dto.LoginRequest;
import com.vicgroup.veterinaria.dto.FaceLoginRequest;

// Import your custom response DTO
import com.vicgroup.veterinaria.dto.JwtResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller


import com.vicgroup.veterinaria.dto.UserDto;
import com.vicgroup.veterinaria.model.User;


/**
 * REST Controller for handling user authentication and registration requests.
 * All endpoints under this controller are prefixed with "/api/auth".
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @PostMapping("/register/admin")
    public JwtResponse regAdmin(@RequestBody @Valid RegisterAdminRequest r) {
        return auth.registerAdmin(r);
    }

    @PostMapping("/register/vet")
    public JwtResponse regVet(@RequestBody @Valid RegisterVetRequest r) {
        return auth.registerVet(r);
    }

    @PostMapping("/register/owner")
    public JwtResponse regOwner(@RequestBody @Valid RegisterOwnerRequest r) {
        return auth.registerOwner(r);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Valid LoginRequest r) {
        return auth.login(r);
    }

    @PostMapping("/login/vet/face")
    public JwtResponse faceLogin(@RequestBody @Valid FaceLoginRequest r) {
        return auth.faceLogin(r);
    }

//    @GetMapping("/whoami")
//    public String whoami() {
//        //can be edited from AuthService in services
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return auth.getPrincipal().toString();
//    }
    @GetMapping("/whoami")
    public UserDto whoami() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserDto(user.getId(), user.getUsername(), user.getRole().getName());
    }
}
