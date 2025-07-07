package com.vicgroup.veterinaria.model;


import com.vicgroup.veterinaria.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_historical_record")
@Getter @Setter @NoArgsConstructor
public class PetHistoricalRecord extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "pet_id", unique = true)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
