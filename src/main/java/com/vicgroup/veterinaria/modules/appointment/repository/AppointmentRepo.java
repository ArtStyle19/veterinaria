package com.vicgroup.veterinaria.modules.appointment.repository;

import com.vicgroup.veterinaria.modules.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    List<Appointment> findByRecordId(Long recordId);


}
