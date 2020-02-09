package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {

    @Query(value = "select * from operation_type r where r.deleted = false", nativeQuery = true)
    List<OperationType> findAllActive();

    @Query(value = "select * from operation_type r where r.clinic_id = ?1 and r.deleted = false", nativeQuery = true)
    List<OperationType> findAllActiveOfClinic(Long clinicId);
}
