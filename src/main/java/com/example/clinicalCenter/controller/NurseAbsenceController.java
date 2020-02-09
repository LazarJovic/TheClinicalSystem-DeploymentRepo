package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.AbsenceCalendarDTO;
import com.example.clinicalCenter.dto.AbsenceDTO;
import com.example.clinicalCenter.dto.AbsenceForListDTO;
import com.example.clinicalCenter.dto.CreateAbsenceDTO;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.service.NurseAbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nurse-absences")
public class NurseAbsenceController {

    @Autowired
    private NurseAbsenceService nurseAbsenceService;

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllWaitingAbsences() {
        List<AbsenceForListDTO> retVal = null;

        try {
            retVal = this.nurseAbsenceService.findAllWaiting();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping("/accept-request/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id) {
        AbsenceForListDTO request = nurseAbsenceService.acceptRequest(id);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping("/deny-request")
    public ResponseEntity<?> denyRequest(@RequestBody AbsenceForListDTO absence) {
        AbsenceForListDTO request = nurseAbsenceService.denyRequest(absence);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/my-absences")
    public ResponseEntity<?> getByLoggedIn() {
        List<AbsenceDTO> retVal = null;

        try {
            retVal = this.nurseAbsenceService.findByLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/{id}/calendar")
    public ResponseEntity<?> getAbsenceForCalendar(@PathVariable Long id) {
        try {
            AbsenceCalendarDTO retVal = this.nurseAbsenceService.findOneForCalendar(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/calendar-absences")
    public ResponseEntity<?> getAbsencesOfLoggedInForCalendar() {
        List<AbsenceCalendarDTO> retVal = null;

        try {
            retVal = this.nurseAbsenceService.findApprovedAbsencesOfLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @PostMapping
    public ResponseEntity<?> createNurseAbsence(@RequestBody CreateAbsenceDTO dto) {
        CreateAbsenceDTO retVal = null;

        try {
            retVal = this.nurseAbsenceService.sendAbsenceRequest(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
