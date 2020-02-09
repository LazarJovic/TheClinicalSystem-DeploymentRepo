package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select * from room r where r.clinic_id = ?1 and r.deleted = false", nativeQuery = true)
    List<Room> findRoomsOfClinic(Long clinicId);

    @Query(value = "select * from room r where r.type = ?1 and r.deleted = false", nativeQuery = true)
    List<Room> findAllByType(int type);

    @Query(value = "select * from room r where r.type = ?1 and clinic_id = ?2 and r.deleted = false", nativeQuery = true)
    List<Room> findAllByTypeAndClinic(int type, Long clinicId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Room findChosenRoom(@Param("id") Long id);
}
