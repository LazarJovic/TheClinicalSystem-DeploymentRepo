package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.NurseAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NurseAbsenceRepository extends JpaRepository<NurseAbsence, Long> {

    @Query(value = "select * from nurse_absence a where a.status = 0", nativeQuery = true)
    List<NurseAbsence> findAllWaiting();

    @Query(value = "select * from nurse_absence a where a.nurse_id = ?1 and a.status in (1)", nativeQuery = true)
    List<NurseAbsence> findAcceptedByStaff(Long staff_id);

    @Query(value = "select * from nurse_absence a where a.nurse_id = ?1 and a.status in (0,1)", nativeQuery = true)
    List<NurseAbsence> findByStaff(Long staff_id);
}
