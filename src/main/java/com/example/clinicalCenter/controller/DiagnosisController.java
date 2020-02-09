package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.DiagnosisDTO;
import com.example.clinicalCenter.service.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;

    @PreAuthorize("hasAnyRole('ROLE_CENTER_ADMIN', 'ROLE_DOCTOR')")
    @GetMapping()
    public ResponseEntity<?> getDiagnoses() {
        List<DiagnosisDTO> list = diagnosisService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody DiagnosisDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DiagnosisDTO retVal = null;

        try {
            retVal = this.diagnosisService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
