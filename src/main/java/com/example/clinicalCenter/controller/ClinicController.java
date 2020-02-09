package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    @Autowired
    private ClinicService service;

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClinicDTO>> getClinics() {
        List<ClinicDTO> requestsDTO = service.findAll();

        return new ResponseEntity<>(requestsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/clinics-for-search-list")
    public ResponseEntity<List<ClinicSearchListDTO>> getClinicsForSearchList() {
        List<ClinicSearchListDTO> requestsDTO = this.service.findAllForSearchList();

        return new ResponseEntity<>(requestsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClinicDTO> getClinic(@PathVariable Long id) {
        ClinicDTO request = service.findOne(id);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PostMapping
    public ResponseEntity<ClinicDTO> createClinic(@RequestBody ClinicDTO dto) {
        ClinicDTO clinic = service.create(dto);
        if (clinic == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(clinic, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping(value = "/get-current-clinic")
    public ResponseEntity<ClinicDTO> getCurrentClinic() {
        return new ResponseEntity<>(this.service.getCurrentClinic(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateClinic(@RequestBody ClinicDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ClinicDTO retVal = null;

        try {
            retVal = this.service.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Cannot edit clinic at this moment.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("/get-clinic-rate")
    public ResponseEntity<?> getClinicRate() {
        return new ResponseEntity<>(this.service.getClinicRate(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/search-clinics")
    public ResponseEntity<?> searchClinics(@RequestBody SearchClinicDTO searchClinic) {
        if (searchClinic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ClinicSearchListDTO> retVal = null;

        try {
            retVal = this.service.searchClinic(searchClinic);

            if (retVal == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/clinic-doctors")
    public ResponseEntity<?> getClinicsDoctorsForSearch(@RequestBody SearchClinicDTO searchClinic) {
        if (searchClinic.clinicId != null) {
            List<Doctor> doctors = this.service.getValidDostors(searchClinic.clinicId, searchClinic);
            List<DoctorSearchListDTO> retVal = this.service.getDoctorsOfClinicForSearchList(doctors, searchClinic.examinationDate, searchClinic.startTime, searchClinic.endTime);
            if (retVal == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/search-doctors-patient")
    public ResponseEntity<?> searchDoctorsPatient(@RequestBody SearchDoctorPatientDTO searchDoctor) {
        if (searchDoctor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<DoctorSearchListDTO> retVal = null;

        try {
            retVal = this.service.searchDoctorsPatient(searchDoctor);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
