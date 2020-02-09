package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.ExaminationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExaminationRequestRepository extends JpaRepository<ExaminationRequest, Long> {

    @Query(value = "select * from examination_requests r where r.status = 0 and r.clinic_id = ?1", nativeQuery = true)
    List<ExaminationRequest> findAllRequestsForAdmin(Long clinicId);

    @Query(value = "select * from examination_requests r where r.status = 0", nativeQuery = true)
    List<ExaminationRequest> findAllWaitingForAdmin();

}
