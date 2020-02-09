package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.service.MedicalRecordService;
import com.example.clinicalCenter.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PatientEditDTO dto = this.patientService.findOneForEdit(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getPatientForEdit(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PatientEditDTO dto = this.patientService.findOneForEdit(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PutMapping
    public ResponseEntity<?> updatePatient(@RequestBody PatientEditDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        PatientEditDTO retVal = null;

        try {
            retVal = this.patientService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR', 'ROLE_NURSE')")
    @GetMapping("/medical-record/{id}")
    public ResponseEntity<?> getMedicalRecord(@PathVariable Long id) {
        MedicalRecordDTO retVal = medicalRecordService.findOne(id);
        if (retVal == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR', 'ROLE_NURSE')")
    @GetMapping("/{id}/medical-record")
    public ResponseEntity<?> getMedicalRecordOfPatient(@PathVariable Long id) {
        PatientMedicalRecordDTO retVal = null;
        try {
            retVal = medicalRecordService.findByPatient(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR')")
    @PutMapping("/medical-record")
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRecordDTO dto) {
        try {
            MedicalRecordDTO retVal = medicalRecordService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/get-patients-of-clinic")
    public ResponseEntity<?> getPatientsOfClinic() {
        try {
            return new ResponseEntity<>(patientService.findPatientsOfClinic(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_NURSE', 'ROLE_DOCTOR')")
    @PostMapping(value = "/search-patients")
    public ResponseEntity<?> searchPatients(@RequestBody SearchPatientDTO searchPatient) {
        if (searchPatient == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<PatientDTO> retVal = null;

        try {
            retVal = this.patientService.searchPatients(searchPatient);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_NURSE')")
    @GetMapping("/record-access/{id}")
    public ResponseEntity<?> canEmployeeOpenMedicalRecord(@PathVariable Long id) {
        boolean isOk = this.patientService.medicalRecordAccessCheck(id);
        return new ResponseEntity<>(isOk, HttpStatus.OK);
    }
}