package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.BusinessChartDTO;
import com.example.clinicalCenter.dto.ClinicAdminDTO;
import com.example.clinicalCenter.dto.ClinicAdminEditDTO;
import com.example.clinicalCenter.dto.ClinicIncomeDatesDTO;
import com.example.clinicalCenter.service.ClinicAdminService;
import com.example.clinicalCenter.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinic-admins")
public class ClinicAdminController {

    @Autowired
    private ClinicAdminService clinicAdminService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getClinicAdminForEdit(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ClinicAdminEditDTO dto = this.clinicAdminService.findOneForEdit(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<?> addUser(@RequestBody ClinicAdminDTO dto) {
        if (userDetailsService.emailTaken(dto.email)) {
            return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
        }

        ClinicAdminDTO retVal = null;

        try {
            retVal = this.clinicAdminService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody ClinicAdminEditDTO dto) {
        ClinicAdminEditDTO retVal = null;

        try {
            retVal = this.clinicAdminService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("clinic-business-daily")
    public ResponseEntity<?> getClinicBusinessForCharts() {

        List<BusinessChartDTO> charts = null;
        charts = this.clinicAdminService.getDailyReview();
        return new ResponseEntity<>(charts, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("clinic-business-weekly")
    public ResponseEntity<?> getClinicBusinessForChartsWeekly() {

        List<BusinessChartDTO> charts = null;
        charts = this.clinicAdminService.getWeeklyReview();
        return new ResponseEntity<>(charts, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("clinic-business-monthly")
    public ResponseEntity<?> getClinicBusinessForChartsMonthly() {

        List<BusinessChartDTO> charts = null;
        charts = this.clinicAdminService.getMonthlyReview();
        return new ResponseEntity<>(charts, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("clinic-income")
    public ResponseEntity<?> getClinicIncome(@RequestBody ClinicIncomeDatesDTO incomePeriod) {
        double income = this.clinicAdminService.getClinicIncome(incomePeriod);
        return new ResponseEntity<>(income, HttpStatus.OK);
    }
}
