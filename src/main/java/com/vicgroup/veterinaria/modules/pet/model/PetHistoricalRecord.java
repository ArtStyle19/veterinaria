package com.vicgroup.veterinaria.modules.pet.model;


import com.vicgroup.veterinaria.core.util.BaseEntity;
import com.vicgroup.veterinaria.modules.user.model.User;
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
