package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, Long> {

    Nurse findByEmail(String email);
}
