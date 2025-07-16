package com.vicgroup.veterinaria.model;

import com.vicgroup.veterinaria.core.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "appointment")
@Getter @Setter @NoArgsConstructor
public class Appointment extends BaseEntity {

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    private Instant date;
    private BigDecimal weight;
//    private Double weight;
    private BigDecimal temperature;
//    private Double temperature;

    @Column(name = "heart_rate")
    private Short heartRate;

//    private Integer heartRate;

    private String description;
    private String treatments;
    private String diagnosis;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
