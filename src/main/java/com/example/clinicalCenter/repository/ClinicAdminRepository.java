package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.ClinicAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicAdminRepository extends JpaRepository<ClinicAdmin, Long> {

    ClinicAdmin findByEmail(String email);
}
