package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.DoctorAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorAbsenceRepository extends JpaRepository<DoctorAbsence, Long> {

    @Query(value = "select * from doctor_absence a where a.doctor_id = ?1 and a.status in (0,1,2)", nativeQuery = true)
    List<DoctorAbsence> findByStaff(Long staff_id);

    @Query(value = "select * from doctor_absence a where a.doctor_id = ?1 and a.status in (1)", nativeQuery = true)
    List<DoctorAbsence> findAcceptedByStaff(Long staff_id);

    @Query(value = "select * from doctor_absence a where a.status = 0", nativeQuery = true)
    List<DoctorAbsence> findAllWaiting();
}
