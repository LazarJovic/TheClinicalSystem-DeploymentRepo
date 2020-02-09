package com.example.clinicalCenter.controller;


import com.example.clinicalCenter.dto.ClinicalCenterAdminDTO;
import com.example.clinicalCenter.dto.ClinicalCenterAdminEditDTO;
import com.example.clinicalCenter.service.ClinicalCenterAdminService;
import com.example.clinicalCenter.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/center-admins")
public class ClinicalCenterAdminController {

    @Autowired
    private ClinicalCenterAdminService clinicalCenterAdminService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<?> addUser(@RequestBody ClinicalCenterAdminDTO dto) {
        if (userDetailsService.emailTaken(dto.email)) {
            return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
        }

        ClinicalCenterAdminDTO retVal = null;

        try {
            retVal = this.clinicalCenterAdminService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getCenterAdminForEdit(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ClinicalCenterAdminEditDTO dto = this.clinicalCenterAdminService.findOneForEdit(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody ClinicalCenterAdminEditDTO dto) {
        ClinicalCenterAdminEditDTO retVal = null;

        try {
            retVal = this.clinicalCenterAdminService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
