package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.ExaminationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExaminationTypeRepository extends JpaRepository<ExaminationType, Long> {

    @Query(value = "select * from examination_type r where r.deleted = false", nativeQuery = true)
    List<ExaminationType> findAllActive();

    @Query(value = "select * from examination_type r where r.clinic_id = ?1 and r.deleted = false", nativeQuery = true)
    List<ExaminationType> findAllActiveOfClinic(Long clinicId);
}
