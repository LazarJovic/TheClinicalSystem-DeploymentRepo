package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.ExaminationRequestDTO;
import com.example.clinicalCenter.service.ExaminationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examination-requests")
public class ExaminationRequestController {

    @Autowired
    private ExaminationRequestService examinationRequestService;

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/create-examination-request")
    public ResponseEntity<?> createExaminationRequestPatient(@RequestBody ExaminationRequestDTO dto) {

        try {
            ExaminationRequestDTO examinationRequest = this.examinationRequestService.createExaminationRequestPatient(dto);
            if (examinationRequest == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(examinationRequest, HttpStatus.CREATED);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping
    public ResponseEntity<?> createExaminationRequest(@RequestBody CreateAppointmentDTO dto) throws Exception {
        ExaminationRequestDTO retVal = null;

        try {
            retVal = this.examinationRequestService.createExaminationRequest(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<AppointmentForListDTO> examinationTypes = examinationRequestService.getRequestsOfClinic();

        return new ResponseEntity<>(examinationTypes, HttpStatus.OK);

    }
}
