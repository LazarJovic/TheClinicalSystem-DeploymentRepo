package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.ClinicRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ClinicRatingRepository extends JpaRepository<ClinicRating, Long> {

    @Query(value = "select * from clinic_rating c where c.clinic_id = ?1 and c.patient_id = ?2", nativeQuery = true)
    ClinicRating findByPatientAndClinic(Long clinicId, Long patientId);

    @Query(value = "select avg(rating) from clinic_rating cr where cr.clinic_id = ?1", nativeQuery = true)
    double clinicRatingAverage(Long clinicId);
}
