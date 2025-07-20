package com.vicgroup.veterinaria.modules.auth.dto.request;
import lombok.Data;

@Data
public class FaceLoginRequest {
    private Long vetId;
    private String imageBase64;
}