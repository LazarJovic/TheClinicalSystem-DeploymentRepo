package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    @Query(value = "select * from examination e where e.clinic_id = ?1", nativeQuery = true)
    List<Examination> findByClinic(Long clinic_id);

    @Query(value = "select * from examination e where e.status in (1,2) and e.doctor_id = ?1", nativeQuery = true)
    List<Examination> findScheduledAndInProgressExaminationsOfDoctor(Long doctorId);

    @Query(value = "select * from examination e where e.status = 2 and e.doctor_id = ?1", nativeQuery = true)
    List<Examination> findInProgressExaminationsOfDoctor(Long doctorId);

    @Query(value = "select * from examination e where e.status in (0,1,2) and e.nurse_id = ?1", nativeQuery = true)
    List<Examination> findAvailableAndScheduledAndInProgressExaminationsOfNurse(Long nurseId);

    @Query(value = "select * from examination e where e.status in (1,2) and e.room_id = ?1", nativeQuery = true)
    List<Examination> findScheduledAndInProgressExaminationsOfRoom(Long roomId);

    @Query(value = "select * from examination e where e.examination_type_id = ?1 and e.status in (0,1,2)", nativeQuery = true)
    List<Examination> findByExaminationTypeAndStatus(Long id);

    @Query(value = "select * from examination e where e.status in (0,1,2) and e.examination_type_id = ?1", nativeQuery = true)
    List<Examination> findScheduledAndInProgressExaminationsOfType(Long id);

    @Query(value = "select * from examination e where e.doctor_id = ?1", nativeQuery = true)
    List<Examination> findExaminationsOfDoctor(Long doctorId);

    @Query(value = "select * from examination e where e.nurse_id = ?1", nativeQuery = true)
    List<Examination> findExaminationsOfNurse(Long nurseId);

    @Query(value = "select * from examination e where e.status in (0,1,2) and e.doctor_id = ?1", nativeQuery = true)
    List<Examination> findAllActiveExaminationsOfDoctor(Long doctorId);

    @Query(value = "select * from examination e where e.status in (0,1,2) and e.nurse_id = ?1", nativeQuery = true)
    List<Examination> findAllActiveExaminationsOfNurse(Long nurseId);

    @Query(value = "select * from examination e where e.predefined = true and e.status = 0 and e.clinic_id = ?1", nativeQuery = true)
    List<Examination> findAvailablePredefinedExaminationsOfClinic(Long clinicId);

    @Query(value = "select * from examination e where e.status = 3 and e.patient_id = ?1", nativeQuery = true)
    List<Examination> findAllFinishedExaminationsOfPatient(Long patientId);

    @Query(value = "select * from examination e where e.status in (2, 3) and e.doctor_id = ?1 and e.patient_id = ?2", nativeQuery = true)
    List<Examination> findAllPatientDoctorFinishedOrInProgressExaminations(Long doctorId, Long patientId);

    @Query(value = "select * from examination e where e.status in (2, 3) and e.nurse_id = ?1 and e.patient_id = ?2", nativeQuery = true)
    List<Examination> findAllPatientNurseFinishedOrInProgressExaminations(Long nurseId, Long patientId);

    @Query(value = "select * from examination e where e.status = 3 and e.clinic_id = ?1", nativeQuery = true)
    List<Examination> findAllFinishedExaminationsOfClinic(Long clinicId);

    @Query(value = "select * from examination e where e.status in (1,2,3) and e.clinic_id = ?1", nativeQuery = true)
    List<Examination> findAllScheduledAndInProgressAndFinishedExaminationsOfClinic(Long clinicId);

    @Query(value = "select * from examination e where e.status = 1 and e.doctor_id = ?1", nativeQuery = true)
    List<Examination> getAllScheduledExaminationsOfDoctor(Long doctorId);

    @Query(value = "select * from examination e where e.status in (0,1,2) and e.room_id = ?1", nativeQuery = true)
    List<Examination> findAllActiveExaminationsOfRoom(Long roomId);

    @Query(value = "select * from examination e where e.status in (0,1,2) and e.room_id = ?1 order by e.start_date_time", nativeQuery = true)
    List<Examination> findAllActiveExaminationsOfRoomSorted(Long roomId);

    @Query(value = "select * from examination e where e.status = 5 and e.patient_id = ?1", nativeQuery = true)
    List<Examination> findAllWaitingForPatientOfPatient(Long patientId);
}
