package com.vicgroup.veterinaria.modules.record.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public class HistoricalRecordClinicId implements Serializable {
    private Long recordId;
    private Long clinicId;
}
