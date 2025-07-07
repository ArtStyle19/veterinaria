package com.vicgroup.veterinaria.model;
import com.vicgroup.veterinaria.util.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;


@Entity
@Table(name = "vet_user")
@Getter @Setter @NoArgsConstructor
public class VetProfile {

    @Id                       // share PK with users.id
    private Long id;

    @MapsId
    @OneToOne @JoinColumn(name = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    private String email;
    private String celNum;

//    @Lob
//    private byte[] faceEmbedding;
    @Lob
    @Column(name = "faceEmbedding", columnDefinition = "bytea")
    @JdbcTypeCode(SqlTypes.BINARY)  // Hibernate 6
    private byte[] faceEmbedding;
}
