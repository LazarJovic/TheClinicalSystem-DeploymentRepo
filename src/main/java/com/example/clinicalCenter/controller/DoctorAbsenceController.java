package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.AbsenceCalendarDTO;
import com.example.clinicalCenter.dto.AbsenceDTO;
import com.example.clinicalCenter.dto.AbsenceForListDTO;
import com.example.clinicalCenter.dto.CreateAbsenceDTO;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.service.DoctorAbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-absences")
public class DoctorAbsenceController {

    @Autowired
    private DoctorAbsenceService doctorAbsenceService;

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/my-absences")
    public ResponseEntity<?> getByLoggedIn() {
        List<AbsenceDTO> retVal = null;

        try {
            retVal = this.doctorAbsenceService.findByLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/{id}/calendar")
    public ResponseEntity<?> getAbsenceForCalendar(@PathVariable Long id) {
        try {
            AbsenceCalendarDTO retVal = this.doctorAbsenceService.findOneForCalendar(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/calendar-absences")
    public ResponseEntity<?> getAbsencesOfLoggedInForCalendar() {
        List<AbsenceCalendarDTO> retVal = null;

        try {
            retVal = this.doctorAbsenceService.findApprovedAbsencesOfLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping
    public ResponseEntity<?> createDoctorAbsence(@RequestBody CreateAbsenceDTO dto) {
        CreateAbsenceDTO retVal = null;

        try {
            retVal = this.doctorAbsenceService.sendAbsenceRequest(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllWaitingAbsences() {
        List<AbsenceForListDTO> retVal = null;

        try {
            retVal = this.doctorAbsenceService.findAllWaiting();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping("/accept-request/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id) {
        AbsenceForListDTO request = doctorAbsenceService.acceptRequest(id);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping("/deny-request")
    public ResponseEntity<?> denyRequest(@RequestBody AbsenceForListDTO absence) {
        AbsenceForListDTO request = doctorAbsenceService.denyRequest(absence);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}
