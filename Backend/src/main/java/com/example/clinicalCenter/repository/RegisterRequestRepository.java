package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.RegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface RegisterRequestRepository extends JpaRepository<RegisterRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RegisterRequest r where r.id = :id")
    RegisterRequest findOneLocked(@Param("id") Long id);
}
