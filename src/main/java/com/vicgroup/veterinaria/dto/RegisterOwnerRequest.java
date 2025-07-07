package com.vicgroup.veterinaria.dto;

import lombok.Data;

@Data
public class RegisterOwnerRequest {
    private String username;
    private String password;
    private String email;
    private String celNum;
}