package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
