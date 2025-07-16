package com.vicgroup.veterinaria.model;
import com.vicgroup.veterinaria.core.util.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    /* specialisations */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VetProfile vetProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private OwnerProfile ownerProfile;
}