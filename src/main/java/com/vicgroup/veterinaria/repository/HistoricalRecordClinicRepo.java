package com.vicgroup.veterinaria.repository;
import com.vicgroup.veterinaria.model.HistoricalRecordClinic;
import com.vicgroup.veterinaria.model.HistoricalRecordClinicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@Repository
//public interface HistoricalRecordClinicRepo extends JpaRepository<HistoricalRecordClinic, HistoricalRecordClinicId> {
//    boolean existsByRecordIdAndClinicId(Long recordId, Long clinicId);
//    Optional<HistoricalRecordClinic> findByRecordId(Long recordId);
//}

@Repository
public interface HistoricalRecordClinicRepo
        extends JpaRepository<HistoricalRecordClinic, HistoricalRecordClinicId> {

    boolean existsByRecordIdAndClinicId(Long recordId, Long clinicId);

    /* ① Cambiamos Optional → List  */
    List<HistoricalRecordClinic> findByRecordId(Long recordId);
}
