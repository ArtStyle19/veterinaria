package com.vicgroup.veterinaria.modules.record.repository;
import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.pet.model.PetHistoricalRecord;
import com.vicgroup.veterinaria.modules.record.model.HistoricalRecordClinic;
import com.vicgroup.veterinaria.modules.record.model.HistoricalRecordClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface HistoricalRecordClinicRepo extends JpaRepository<HistoricalRecordClinic, HistoricalRecordClinicId> {
//    boolean existsByRecordIdAndClinicId(Long recordId, Long clinicId);
//    Optional<HistoricalRecordClinic> findByRecordId(Long recordId);
//}

//@Repository
//public interface HistoricalRecordClinicRepo
//        extends JpaRepository<HistoricalRecordClinic, HistoricalRecordClinicId> {
//
//    boolean existsByRecordIdAndClinicId(Long recordId, Long clinicId);
//
//    /* ① Cambiamos Optional → List  */
//    List<HistoricalRecordClinic> findByRecordId(Long recordId);
//}


@Repository
public interface HistoricalRecordClinicRepo
        extends JpaRepository<HistoricalRecordClinic, HistoricalRecordClinicId> {

    boolean existsByRecordIdAndClinicId(Long recordId, Long clinicId);

    List<HistoricalRecordClinic> findByRecordId(Long recordId);

    // ✅ Nuevo método para obtener el nivel de acceso
    Optional<HistoricalRecordClinic> findByRecordIdAndClinicId(Long recordId, Long clinicId);

    List<HistoricalRecordClinic> findByClinicId(Long clinicId);
    // HistoricalRecordClinicRepo.java
    Optional<HistoricalRecordClinic> findFirstByRecordIdAndClinicId(Long recordId, Long clinicId);
//        Optional<HistoricalRecordClinic> findFirstByPetIdAndClinicId(Long petId, Long clinicId);



    @Query("""
        SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END
        FROM HistoricalRecordClinic h
        WHERE h.record.pet.id = :petId
          AND h.clinic.id       = :clinicId
          AND h.accessLevel     = com.vicgroup.veterinaria.core.enums.AccessLevelEnum.FULL
    """)
    boolean hasFullAccess(Long petId, Long clinicId);
}
