package com.vicgroup.veterinaria.model;
import com.vicgroup.veterinaria.core.enums.AccessLevelEnum;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "historical_record_clinic")
@Getter @Setter @NoArgsConstructor
@IdClass(HistoricalRecordClinicId.class)
public class HistoricalRecordClinic {

    @Id
    private Long recordId;

    @Id
    private Long clinicId;

    @Enumerated(EnumType.STRING)
    private AccessLevelEnum accessLevel = AccessLevelEnum.READ;

    private Instant authorizedAt = Instant.now();
}

