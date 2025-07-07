package com.vicgroup.veterinaria.model;
import com.vicgroup.veterinaria.util.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor
public class Role extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;   // ADMIN | VET | PET_OWNER
}