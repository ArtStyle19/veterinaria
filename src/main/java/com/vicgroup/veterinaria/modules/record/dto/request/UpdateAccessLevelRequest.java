package com.vicgroup.veterinaria.modules.record.dto.request;

import lombok.Data;

@Data
public class UpdateAccessLevelRequest {
    private Long clinicId;
    private String accessLevel; // Valores válidos: READ, WRITE, FULL, NONE
}
