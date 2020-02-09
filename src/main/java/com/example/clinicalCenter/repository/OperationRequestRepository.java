package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.OperationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationRequestRepository extends JpaRepository<OperationRequest, Long> {

    @Query(value = "select * from operation_requests r where r.status = 0 and r.clinic_id = ?1", nativeQuery = true)
    List<OperationRequest> findAllRequestsForAdmin(Long clinicId);

    @Query(value = "select * from operation_requests r where r.status = 0", nativeQuery = true)
    List<OperationRequest> findAllWaitingForAdmin();
}
