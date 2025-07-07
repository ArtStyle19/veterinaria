package com.vicgroup.veterinaria.dto;
import lombok.Data;

@Data
public class RegisterAdminRequest {
    private String username;
    private String password;
}