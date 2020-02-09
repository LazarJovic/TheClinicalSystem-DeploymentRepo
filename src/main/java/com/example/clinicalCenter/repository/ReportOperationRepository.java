package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.ReportOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportOperationRepository extends JpaRepository<ReportOperation, Long> {

    @Query(value = "select * from operation_reports r where r.medical_record_id = ?1", nativeQuery = true)
    List<ReportOperation> findAllOperationReportsOfMedicalRecord(Long recordId);
}
