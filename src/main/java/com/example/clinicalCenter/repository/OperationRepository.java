package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    @Query(value = "select * from operation o where o.clinic_id = ?1", nativeQuery = true)
    List<Operation> findByClinic(Long clinic_id);

    @Query(value = "select * from operation o where o.status in (0,1) and o.operation_type_id = ?1", nativeQuery = true)
    List<Operation> findScheduledAndInProgressOperationsOfType(Long id);

    @Query(value = "select * from operation o where o.status = 3 and o.patient_id = ?1", nativeQuery = true)
    List<Operation> findAllFinishedOperationsOfPatient(Long patientId);

    @Query(value = "select * from operation o, operation_doctors od where o.id = od.operation_id and o.status in (1,2) and od.doctors_id = ?1", nativeQuery = true)
    List<Operation> findScheduledAndInProgressOperationsOfDoctor(Long id);

    @Query(value = "select * from operation o, operation_doctors od where o.id = od.operation_id and o.status in (0,1,2) and od.doctors_id = ?1", nativeQuery = true)
    List<Operation> findAllActiveOperationsOfDoctor(Long id);

    @Query(value = "select * from operation o, operation_doctors od where o.id = od.operation_id and o.status in (2,3) and o.patient_id = ?2 and od.doctors_id = ?1", nativeQuery = true)
    List<Operation> findFinishedAndInProgressOperationsPatientDoctor(Long doctorId, Long patientId);

    @Query(value = "select * from operation o where o.status = 3 and o.clinic_id = ?1", nativeQuery = true)
    List<Operation> findAllFinishedOperationsOfClinic(Long clinicId);

    @Query(value = "select * from operation o where o.status in (1,2,3) and o.clinic_id = ?1", nativeQuery = true)
    List<Operation> findAllScheduledAndInProgressAndFinishedOperationsOfClinic(Long clinicId);

    @Query(value = "select * from operation o, operation_doctors od where o.id = od.operation_id and o.status = 1 and od.doctors_id = ?1", nativeQuery = true)
    List<Operation> getAllScheduledOperationsOfDoctor(Long id);

    @Query(value = "select * from operation o where o.status = 5 and o.patient_id = ?1", nativeQuery = true)
    List<Operation> findAllWaitingForPatientOfPatient(Long patientId);

    @Query(value = "select doctors_id from operation_doctors od where od.operation_id = ?1", nativeQuery = true)
    List<Long> findAllDoctorsOfOperation(Long operationId);

    @Query(value = "select * from operation o where o.status in (0,1,2) and o.room_id = ?1", nativeQuery = true)
    List<Operation> findAllActiveOperationsOfRoom(Long id);

    @Query(value = "select * from operation o where o.status in (0,1,2) and o.room_id = ?1 order by o.start_date_time", nativeQuery = true)
    List<Operation> findAllActiveOperationsOfRoomSorted(Long roomId);

    @Query(value = "select * from operation o, operation_doctors od where o.id = od.operation_id and o.status = 2 and od.doctors_id = ?1", nativeQuery = true)
    List<Operation> findInProgressOperationsOfDoctor(Long id);
}
