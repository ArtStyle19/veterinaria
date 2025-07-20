package com.vicgroup.veterinaria.modules.symptom.model;

import com.vicgroup.veterinaria.core.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "symptoms")
@Getter @Setter @NoArgsConstructor
public class Symptom extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;
}
