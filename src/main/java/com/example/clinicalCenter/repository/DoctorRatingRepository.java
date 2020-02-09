package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DoctorRatingRepository extends JpaRepository<DoctorRating, Long> {

    @Query(value = "select * from doctor_rating d where d.doctor_id = ?1 and d.patient_id = ?2", nativeQuery = true)
    DoctorRating findByPatientAndDoctor(Long doctorId, Long patientId);

    @Query(value = "select avg(rating) from doctor_rating cr where cr.doctor_id = ?1", nativeQuery = true)
    double doctorRatingAverage(Long doctorId);
}
