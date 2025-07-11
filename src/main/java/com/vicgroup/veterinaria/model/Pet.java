package com.vicgroup.veterinaria.model;

import com.vicgroup.veterinaria.model.enums.PetStatusEnum;
import com.vicgroup.veterinaria.model.enums.SexEnum;
import com.vicgroup.veterinaria.model.enums.VisibilityEnum;

import com.vicgroup.veterinaria.util.BaseEntity;
//import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pet")
@Getter @Setter @NoArgsConstructor
public class Pet extends BaseEntity {

    private String name;
    private String species;
    private String breed;


    @Enumerated(EnumType.STRING)
    @Column(name = "sex", columnDefinition = "sex_enum")

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SexEnum sex;


//    @Enumerated(EnumType.STRING)
//    @Column(name = "sex", columnDefinition = "sex_enum")
//    @Type(PostgreSQLEnumType.class)
//    private SexEnum sex;


    private LocalDate birthdate;

//    @Enumerated(EnumType.STRING)
//    private PetStatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "pet_status_enum")

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PetStatusEnum status;

    @Column(name = "home_clinic_id")
    private Long homeClinicId;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_contact")
    private String ownerContact;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @Column(name = "qr_code_token", nullable = false, unique = true)
    private UUID qrCodeToken = UUID.randomUUID();

    @Column(name = "edit_code", nullable = false)
    private String editCode;


//    @Enumerated(EnumType.STRING)
//    private VisibilityEnum visibility = VisibilityEnum.CLINIC_ONLY;
//

//    @Enumerated(EnumType.STRING)
//    @Column(name = "visibility", columnDefinition = "visibility_enum")
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
//    private VisibilityEnum visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", columnDefinition = "visibility_enum")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private VisibilityEnum visibility = VisibilityEnum.CLINIC_ONLY;

}
