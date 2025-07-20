package com.vicgroup.veterinaria.modules.auth.dto.request;

import lombok.Data;

@Data
public class RegisterVetRequest {
    private String username;
    private String password;
    private Long clinicId;
    private String celNum;
    private String email;
}