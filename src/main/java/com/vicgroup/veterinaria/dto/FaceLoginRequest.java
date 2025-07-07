package com.vicgroup.veterinaria.dto;
import lombok.Data;

@Data
public class FaceLoginRequest {
    private Long vetId;
    private String imageBase64;
}