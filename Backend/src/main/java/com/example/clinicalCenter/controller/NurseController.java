package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.ExaminationParamsDTO;
import com.example.clinicalCenter.dto.NurseDTO;
import com.example.clinicalCenter.dto.NurseEditDTO;
import com.example.clinicalCenter.dto.SearchNurseDTO;
import com.example.clinicalCenter.service.CustomUserDetailsService;
import com.example.clinicalCenter.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/nurses")
public class NurseController {

    @Autowired
    private NurseService nurseService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody NurseDTO dto) {
        if (userDetailsService.emailTaken(dto.email)) {
            return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
        }

        NurseDTO retVal = null;

        try {
            retVal = this.nurseService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getNurseForEdit(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        NurseEditDTO dto = this.nurseService.findOneForEdit(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeNurse(@PathVariable Long id) {
        NurseDTO nurse = null;
        try {
            nurse = this.nurseService.delete(id);
            return new ResponseEntity<>(nurse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @PutMapping("/update")
    public ResponseEntity<?> updateNurse(@RequestBody NurseEditDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        NurseEditDTO retVal = null;

        try {
            retVal = this.nurseService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/patients")
    public ResponseEntity<?> getPatientsOfClinic() {
        try {
            return new ResponseEntity<>(nurseService.findPatientsOfClinic(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/get-free-nurses")
    public ResponseEntity<?> allFreeNurses(@RequestBody ExaminationParamsDTO examinationParams) {
        List<NurseDTO> freeNurses = null;
        try {
            freeNurses = this.nurseService.getFreeNurses(examinationParams);
            return new ResponseEntity<>(freeNurses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping(value = "/logged-in")
    public ResponseEntity<?> getUsernameOfLoggedIn() {
        NurseDTO dto = this.nurseService.getLoggedIn();
        if (dto == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping(value = "/nurses-of-clinic")
    public ResponseEntity<?> getNursesOfClinic() {
        ArrayList<NurseEditDTO> nursesOfClinic = null;

        try {
            nursesOfClinic = this.nurseService.getNursesOfClinic();
            return new ResponseEntity<>(nursesOfClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/search-nurses")
    public ResponseEntity<?> searchNurses(@RequestBody SearchNurseDTO searchNurse) {
        if (searchNurse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<NurseEditDTO> retVal = null;

        try {
            retVal = this.nurseService.searchNurses(searchNurse);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping(value = "/review-reports")
    public ResponseEntity<?> getReportsToReview() {
        try {
            return new ResponseEntity<>(this.nurseService.findReportsToReview(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
