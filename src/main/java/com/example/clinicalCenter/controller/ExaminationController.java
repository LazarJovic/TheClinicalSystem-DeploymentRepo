package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/examinations")
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_CLINIC_ADMIN')")  // proveriti
    @GetMapping("/{id}")
    public ResponseEntity<?> getExamination(@PathVariable Long id) {
        try {
            ExaminationDTO retVal = this.examinationService.findOne(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createExamination(@RequestBody ExaminationDTO dto) {
        ExaminationDTO retVal = null;

        try {
            retVal = this.examinationService.create(dto);
            if (retVal == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_NURSE')")  // proveriti
    @GetMapping("/calendar-examinations")
    public ResponseEntity<?> getExaminationsOfLoggedIn() {
        List<ExaminationCalendarDTO> retVal = null;

        try {
            retVal = this.examinationService.getExaminationsOfLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN', 'ROLE_PATIENT')")
    @GetMapping(value = "/predefined-examinations/{clinicId}")
    public ResponseEntity<?> getAllPredefinedExaminationsOfClinic(@PathVariable Long clinicId) {

        ArrayList<PredefinedExaminationDTO> predefinedExams = null;

        try {
            predefinedExams = this.examinationService.getAllPredefinedExaminationsOfClinic(clinicId);
            return new ResponseEntity<>(predefinedExams, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_NURSE')")
    @GetMapping("/{id}/calendar")
    public ResponseEntity<?> getExaminationCalendar(@PathVariable Long id) {
        try {
            ExaminationCalendarDetailedDTO retVal = this.examinationService.findOneForCalendar(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PutMapping(value = "/schedule-examination")
    public ResponseEntity<?> scheduleExamination(@RequestBody PredefinedExaminationDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PredefinedExaminationDTO retVal = null;

        try {
            retVal = this.examinationService.schedule(dto);
            System.out.println(retVal);
            if (retVal == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (OptimisticLockException oe) {
            return new ResponseEntity<>("This examination has already been scheduled.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PutMapping(value = "/cancel-examination")
    public ResponseEntity<?> cancelExamination(@RequestBody PredefinedExaminationDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        PredefinedExaminationDTO retVal = null;

        try {
            retVal = this.examinationService.cancel(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/scheduled-examinations")
    public ResponseEntity<?> getAllScheduledExaminations() {

        ArrayList<PredefinedExaminationDTO> predefinedExams = null;

        try {
            predefinedExams = this.examinationService.getAllScheduledExaminations();
            return new ResponseEntity<>(predefinedExams, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/finished-examinations")
    public ResponseEntity<?> getAllFinishedExaminations() {

        ArrayList<PredefinedExaminationDTO> finishedExams = null;

        try {
            finishedExams = this.examinationService.getAllFinishedExaminations();
            return new ResponseEntity<>(finishedExams, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping(value = "/get-upcoming")
    public ResponseEntity<?> getUpcomingExaminations() {
        List<AppointmentDTO> upcomingExaminations = null;

        try {
            upcomingExaminations = this.examinationService.getAllUpcomingExaminations();
            return new ResponseEntity<>(upcomingExaminations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping(value = "/cancel-examination-doctor")
    public ResponseEntity<?> cancelExaminationDoctor(@RequestBody AppointmentDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AppointmentDTO retVal = null;

        try {
            retVal = this.examinationService.doctorCanceling(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping(value = "/start-examination-doctor/{id}")
    public ResponseEntity<?> startExaminationDoctor(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AppointmentDTO retVal = null;

        try {
            retVal = this.examinationService.startExaminationFromId(id);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping(value = "/start-examination-doctor")
    public ResponseEntity<?> startExaminationDoctor(@RequestBody AppointmentDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AppointmentDTO retVal = null;

        try {
            retVal = this.examinationService.startExamination(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")       // proveriti
    @PutMapping(value = "/can-examination-start")
    public ResponseEntity<?> canExaminationStart(@RequestBody Long examinationId) {
        if (examinationId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        try {
            boolean retVal = this.examinationService.canExaminationStart(examinationId);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/get-rating-info/{id}")
    public ResponseEntity<?> getInfoForRating(@PathVariable Long id) {
        try {
            RatingDoctorAndClinicDTO retVal = this.examinationService.getInfoForRating(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN', 'ROLE_PATIENT')")
    // PROVERITI nije rola Patient, zar ne? Ili jeste?
    @GetMapping(value = "/waiting-for-patient")
    public ResponseEntity<?> getWaitingForPatientExaminations() {
        List<AppointmentDTO> waitingForPatient = null;

        try {
            waitingForPatient = this.examinationService.getWaitingForPatientExaminations();
            return new ResponseEntity<>(waitingForPatient, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN', 'ROLE_PATIENT')")   // proveriti
    @PutMapping("/confirm-examination")
    public ResponseEntity<?> confirmExamination(@RequestBody Long id) {
        AppointmentDTO examination = examinationService.confirmExamination(id);
        if (examination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(examination, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN', 'ROLE_PATIENT')")  // proveriti
    @PutMapping("/deny-examination")
    public ResponseEntity<?> denyExamination(@RequestBody Long id) {
        AppointmentDTO examination = examinationService.denyExamination(id);
        if (examination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(examination, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping(value = "/in-progress-doctor")
    public ResponseEntity<?> getExaminationsInProgressOfDoctor() {
        try {
            List<ExaminationDTO> retVal = this.examinationService.getExaminationsInProgressOfLoggedIn();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
