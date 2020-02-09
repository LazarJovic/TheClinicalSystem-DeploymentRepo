package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "select * from reports r, examination e where e.nurse_id = ?1 and r.reviewed = false", nativeQuery = true)
    List<Report> findUnreviewedReportsOfNurse(Long nurseId);

    @Query(value = "select * from reports r where r.medical_record_id = ?1", nativeQuery = true)
    List<Report> findAllExaminationReportsOfMedicalRecord(Long recordId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Report r where r.id = :id")
    Report findOneLocked(@Param("id") Long id);
}
