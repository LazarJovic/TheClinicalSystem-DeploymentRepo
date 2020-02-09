package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}
