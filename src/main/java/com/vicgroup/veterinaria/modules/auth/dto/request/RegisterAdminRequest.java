package com.vicgroup.veterinaria.modules.auth.dto.request;
import lombok.Data;

@Data
public class RegisterAdminRequest {
    private String username;
    private String password;
}