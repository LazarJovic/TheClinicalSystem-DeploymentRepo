package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByEmail(String email);

}
