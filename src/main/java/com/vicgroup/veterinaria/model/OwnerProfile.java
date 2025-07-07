package com.vicgroup.veterinaria.model;
import com.vicgroup.veterinaria.util.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "pet_owner_user")
@Getter @Setter @NoArgsConstructor
public class OwnerProfile {

    @Id private Long id;

    @MapsId
    @OneToOne @JoinColumn(name = "id")
    private User user;

    private String email;
    private String celNum;
}
