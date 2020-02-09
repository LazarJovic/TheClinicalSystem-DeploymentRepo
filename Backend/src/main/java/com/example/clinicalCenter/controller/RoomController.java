package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody RoomDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        RoomDTO retVal = null;

        try {
            retVal = this.roomService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Do we need this???
    @GetMapping("/getOne/{id}")
    public ResponseEntity<RoomDTO> getOne(@PathVariable long id) {
        return new ResponseEntity<>(this.roomService.findOne(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping(value = "/rooms-of-clinic")
    public ResponseEntity<?> getRoomsOfClinic() {
        ArrayList<RoomDTO> roomsOfClinic = null;

        try {
            roomsOfClinic = this.roomService.getRoomsOfClinic();
            return new ResponseEntity<>(roomsOfClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeRoom(@PathVariable Long id) {
        RoomDTO room = null;
        try {
            room = this.roomService.delete(id);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Room has already been removed by another clinic admin.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateRoom(@RequestBody RoomDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        RoomDTO retVal = null;

        try {
            retVal = this.roomService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Cannot edit room at this moment.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/get-free-rooms")
    public ResponseEntity<?> allFreeRooms(@RequestBody ExaminationParamsDTO examinationParams) {
        List<RoomDTO> freeRooms = null;
        try {
            freeRooms = this.roomService.getFreeRooms(examinationParams);
            return new ResponseEntity<>(freeRooms, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-rooms")
    public ResponseEntity<?> searchRooms(@RequestBody SearchRoomDTO searchRoom) {
        if (searchRoom == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<RoomDTO> retVal = null;

        try {
            retVal = this.roomService.searchRooms(searchRoom);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-rooms-request")
    public ResponseEntity<?> searchRoomsForRequest(@RequestBody AppointmentForListDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<RoomTimeDTO> retVal = null;

        try {
            retVal = this.roomService.searchRoomsForRequestAtGivenTime(dto, true, false);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-rooms-request-extended")
    public ResponseEntity<?> searchRoomsForRequestExtended(@RequestBody AppointmentForListDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<RoomTimeDTO> retVal = null;

        try {
            retVal = this.roomService.searchRoomsForRequestExtended(dto, true, false);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-op-rooms-request")
    public ResponseEntity<?> searchOperatingRoomsForRequest(@RequestBody OperationRequestDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<RoomTimeDTO> retVal = null;

        try {
            AppointmentForListDTO appointmentForListDTO = new AppointmentForListDTO();
            appointmentForListDTO.doctorId = dto.doctor;
            appointmentForListDTO.date = dto.examDate;
            appointmentForListDTO.startTime = dto.startTime;
            appointmentForListDTO.endTime = dto.endTime;
            retVal = this.roomService.searchRoomsForRequestAtGivenTime(appointmentForListDTO, false, false);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-op-rooms-request-extended")
    public ResponseEntity<?> searchOperatingRoomsForRequestExtended(@RequestBody OperationRequestDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<RoomTimeDTO> retVal = null;

        try {
            AppointmentForListDTO appointmentForListDTO = new AppointmentForListDTO();
            appointmentForListDTO.doctorId = dto.doctor;
            appointmentForListDTO.date = dto.examDate;
            appointmentForListDTO.startTime = dto.startTime;
            appointmentForListDTO.endTime = dto.endTime;
            retVal = this.roomService.searchRoomsForRequestExtended(appointmentForListDTO, false, false);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
