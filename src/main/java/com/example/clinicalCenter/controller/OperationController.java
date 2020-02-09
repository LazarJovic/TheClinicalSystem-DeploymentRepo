package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_CLINIC_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOperation(@PathVariable Long id) {
        try {
            OperationDTO retVal = this.operationService.findOne(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping("/{id}/calendar")
    public ResponseEntity<?> getOperationForCalendar(@PathVariable Long id) {
        try {
            OperationCalendarDetailedDTO retVal = this.operationService.findOneForCalendar(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping("/calendar-operations")
    public ResponseEntity<?> getOperationsOfLoggedIn() {
        List<OperationCalendarDTO> retVal = null;

        try {
            retVal = this.operationService.getOperationsOfLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT')")
    @GetMapping(value = "/scheduled-operations")
    public ResponseEntity<?> getAllScheduledOperations() {

        ArrayList<OperationForListDTO> scheduledOperations = null;

        try {
            scheduledOperations = this.operationService.getAllScheduledOperations();
            return new ResponseEntity<>(scheduledOperations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT')")
    @GetMapping(value = "/finished-operations")
    public ResponseEntity<?> getAllFinishedOperations() {

        ArrayList<OperationForListDTO> finishedOperations = null;

        try {
            finishedOperations = this.operationService.getAllFinishedOperations();
            return new ResponseEntity<>(finishedOperations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping(value = "/get-upcoming")
    public ResponseEntity<?> getUpcomingOperations() {
        List<AppointmentDTO> upcomingOperations = null;

        try {
            upcomingOperations = this.operationService.getAllUpcomingOperations();
            return new ResponseEntity<>(upcomingOperations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PutMapping(value = "/cancel-operation-doctor")
    public ResponseEntity<?> cancelOperationDoctor(@RequestBody AppointmentDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AppointmentDTO retVal = null;

        try {
            retVal = this.operationService.doctorCanceling(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT')")
    @GetMapping(value = "/get-rating-info/{id}")
    public ResponseEntity<?> getInfoForRating(@PathVariable Long id) {
        try {
            RatingDoctorAndClinicODTO retVal = this.operationService.getInfoForRating(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PutMapping(value = "/start-operation-doctor/{id}")
    public ResponseEntity<?> startOperationDoctorFromId(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AppointmentDTO retVal = null;

        try {
            retVal = this.operationService.startOperationFromId(id);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PutMapping(value = "/start-operation-doctor")
    public ResponseEntity<?> startOperationDoctor(@RequestBody AppointmentDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AppointmentDTO retVal = null;

        try {
            retVal = this.operationService.startOperation(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PutMapping(value = "/can-operation-start")
    public ResponseEntity<?> canOperationStart(@RequestBody Long operationId) {
        if (operationId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        try {
            boolean retVal = this.operationService.canOperationStart(operationId);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT')")
    @GetMapping(value = "/waiting-for-patient")
    public ResponseEntity<?> getWaitingForPatientOperations() {
        List<AppointmentDTO> waitingForPatient = null;

        try {
            waitingForPatient = this.operationService.getWaitingForPatientOperations();
            return new ResponseEntity<>(waitingForPatient, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT')")
    @PutMapping("/confirm-operation")
    public ResponseEntity<?> confirmOperation(@RequestBody Long id) {
        AppointmentDTO operation = operationService.confirmOperation(id);
        if (operation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(operation, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT')")
    @PutMapping("/deny-operation")
    public ResponseEntity<?> denyOperation(@RequestBody Long id) {
        AppointmentDTO operation = operationService.denyOperation(id);
        if (operation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(operation, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createOperation(@RequestBody OperationDTO dto) {
        OperationDTO retVal = null;

        try {
            retVal = this.operationService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping(value = "/in-progress-doctor")
    public ResponseEntity<?> getOperationsInProgressOfDoctor() {
        try {
            List<OperationDTO> retVal = this.operationService.getOperationsInProgressOfLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
