package com.vicgroup.veterinaria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
