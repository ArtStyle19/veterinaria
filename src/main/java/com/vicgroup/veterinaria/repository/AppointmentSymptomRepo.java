package com.vicgroup.veterinaria.repository;

import com.vicgroup.veterinaria.model.AppointmentSymptom;
import com.vicgroup.veterinaria.model.AppointmentSymptomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

@Repository
public interface AppointmentSymptomRepo extends JpaRepository<AppointmentSymptom, AppointmentSymptomId> {

    @Modifying
    @Transactional
    @Query("INSERT INTO AppointmentSymptom (appointmentId, symptomId) VALUES (:aid, :sid)")
    void saveSymptomLink(@Param("aid") Long aid, @Param("sid") Long sid);
}
