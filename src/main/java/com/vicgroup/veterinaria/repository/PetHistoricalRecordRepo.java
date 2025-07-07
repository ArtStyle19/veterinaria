package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.PetHistoricalRecord;
import com.vicgroup.veterinaria.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetHistoricalRecordRepo extends JpaRepository<PetHistoricalRecord, Long> {
    Optional<PetHistoricalRecord> findByPet(Pet pet);
//    Optional<PetHistoricalRecord> findByPetId(Long petId);
@Query("SELECT h FROM PetHistoricalRecord h " +
        "JOIN HistoricalRecordClinic hrc ON hrc.recordId = h.id " +
        "WHERE h.pet.id = :petId AND hrc.clinicId = :clinicId")
List<PetHistoricalRecord> findByPetIdAndClinicId(@Param("petId") Long petId,
                                                 @Param("clinicId") Long clinicId);

    List<PetHistoricalRecord> findByPetId(Long petId);  // útil si querés todo el historial sin filtro

}