package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.AppointmentForListDTO;
import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.OperationRequestDTO;
import com.example.clinicalCenter.service.OperationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operation-requests")
public class OperationRequestController {

    @Autowired
    private OperationRequestService operationRequestService;

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PostMapping
    public ResponseEntity<?> createOperationRequest(@RequestBody CreateAppointmentDTO dto) throws Exception {
        OperationRequestDTO retVal = null;

        try {
            retVal = this.operationRequestService.createOperationRequest(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<AppointmentForListDTO> examinationTypes = operationRequestService.getRequestsOfClinic();

        return new ResponseEntity<>(examinationTypes, HttpStatus.OK);

    }

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return new ResponseEntity<>(operationRequestService.findOne(id), HttpStatus.OK);
    }

}
