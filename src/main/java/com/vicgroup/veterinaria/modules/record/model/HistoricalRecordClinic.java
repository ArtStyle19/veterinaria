package com.vicgroup.veterinaria.modules.record.model;
import com.vicgroup.veterinaria.core.enums.AccessLevelEnum;

import com.vicgroup.veterinaria.modules.clinic.model.Clinic;
import com.vicgroup.veterinaria.modules.pet.model.PetHistoricalRecord;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "historical_record_clinic")
@IdClass(HistoricalRecordClinicId.class)
@Getter @Setter @NoArgsConstructor
public class HistoricalRecordClinic {

    @Id
    @Column(name = "record_id")
    private Long recordId;

    @Id
    @Column(name = "clinic_id")
    private Long clinicId;

    // 🔑 Relaciones para poder “navegar” en JPQL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", insertable = false, updatable = false)
    private PetHistoricalRecord record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;

    @Enumerated(EnumType.STRING)
    private AccessLevelEnum accessLevel = AccessLevelEnum.READ;

    private Instant authorizedAt = Instant.now();
}

