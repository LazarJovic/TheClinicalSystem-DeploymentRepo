package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Patient findByEmail(String email);
}
