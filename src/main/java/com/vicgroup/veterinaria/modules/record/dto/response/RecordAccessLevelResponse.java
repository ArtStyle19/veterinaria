package com.vicgroup.veterinaria.modules.record.dto.response;

import lombok.Data;

@Data
public class RecordAccessLevelResponse {
    private Long clinicId;
    private String clinicName;
    private String accessLevel;

    public RecordAccessLevelResponse(Long clinicId, String clinicName, String accessLevel) {
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.accessLevel = accessLevel;
    }
}
