package com.patient.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.patient.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByDoctorId(Long id);

    List<Patient> findByDoctorId(Long doctorId);
}