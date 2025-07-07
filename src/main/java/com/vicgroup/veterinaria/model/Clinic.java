package com.vicgroup.veterinaria.model;

import com.vicgroup.veterinaria.util.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "clinic")
@Getter @Setter @NoArgsConstructor
public class Clinic extends BaseEntity {
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String email;
}
