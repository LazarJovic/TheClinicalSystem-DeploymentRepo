package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query(value = "select * from users t where t.type = ?1", nativeQuery = true)
    List<User> findByType(String type);

    @Query(value = "select * from users t where t.type = 'DOCTOR' and t.clinic_id = ?1 and t.deleted = false", nativeQuery = true)
    List<Doctor> findDoctorsOfClinic(Long clinicId);

    @Query(value = "select * from users d where d.type = 'DOCTOR' and d.specialty = ?1 and d.deleted = false", nativeQuery = true)
    List<Doctor> findBySpecialty(Long specialty);

    @Query(value = "select * from users d where d.type = 'DOCTOR' and d.clinic_id = ?1 and d.specialty = ?2 and d.deleted = false", nativeQuery = true)
    List<Doctor> findDoctorsOfClinicBySpecialty(Long clinic_id, Long specialty);

    @Query(value = "select * from users t where t.type = 'NURSE' and t.clinic_id = ?1 and t.deleted = false", nativeQuery = true)
    List<Nurse> findNursesOfClinic(Long clinicId);

    @Query(value = "select * from users t where t.type = 'CLINIC_ADMIN' and t.clinic_id = ?1 and t.deleted = false", nativeQuery = true)
    List<ClinicAdmin> findClinicAdmins(Long clinicId);

    @Query(value = "select * from users t where t.type = 'PATIENT' and t.id = ?1 and t.deleted = false", nativeQuery = true)
    Patient findPatient(Long patId);

    @Query(value = "select * from users t where t.type = 'DOCTOR' and t.id = ?1 and t.deleted = false", nativeQuery = true)
    Doctor findDoctor(Long doctorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select u from User u where u.id = :id and u.deleted = false")
    Doctor findDoctorExaminationDetails(@Param("id") Long id);

}
