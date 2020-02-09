package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.ExaminationReportForListDTO;
import com.example.clinicalCenter.dto.OperationReportForListDTO;
import com.example.clinicalCenter.dto.ReportDTO;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.service.ReportOperationService;
import com.example.clinicalCenter.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportOperationService reportOperationService;

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        ReportDTO retVal = null;

        try {
            retVal = this.reportService.findOne(id);
            if (retVal == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping("operation/{id}")
    public ResponseEntity<?> getReportOperation(@PathVariable Long id) {
        ReportDTO retVal = null;

        try {
            retVal = this.reportOperationService.findOne(id);
            if (retVal == null)
                throw new NotFoundException();
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody ReportDTO dto) {
        ReportDTO retVal = null;

        try {
            retVal = this.reportService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_NURSE')")
    @PutMapping
    public ResponseEntity<?> updateReport(@RequestBody ReportDTO dto) {
        ReportDTO retVal = null;

        try {
            retVal = this.reportService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PostMapping(value = "operation")
    public ResponseEntity<?> createReportOperation(@RequestBody ReportDTO dto) {
        ReportDTO retVal = null;

        try {
            retVal = this.reportOperationService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @PutMapping(value = "operation")
    public ResponseEntity<?> updateReportOperation(@RequestBody ReportDTO dto) {
        ReportDTO retVal = null;

        try {
            retVal = this.reportOperationService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT', 'ROLE_NURSE')")
    @GetMapping(value = "/examination-reports/{recordId}")
    public ResponseEntity<?> getAllExaminationReportsOfMedicalRecord(@PathVariable Long recordId) {

        ArrayList<ExaminationReportForListDTO> examinationReports = null;

        try {
            examinationReports = this.reportService.findAllExaminationReportOfMedicalRecord(recordId);
            return new ResponseEntity<>(examinationReports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_PATIENT', 'ROLE_NURSE')")
    @GetMapping(value = "/operation-reports/{recordId}")
    public ResponseEntity<?> getAllOperationReportsOfMedicalRecord(@PathVariable Long recordId) {

        ArrayList<OperationReportForListDTO> operationReports = null;

        try {
            operationReports = this.reportOperationService.getAllOperationReportsOfMedicalRecord(recordId);
            return new ResponseEntity<>(operationReports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping(value = "/examination-report-access/{id}")
    public ResponseEntity<?> canLoggedInEditReport(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(this.reportService.canLoggedInEditReport(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    @GetMapping(value = "/operation-report-access/{id}")
    public ResponseEntity<?> canLoggedInEditOperationReport(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(this.reportOperationService.canLoggedInEditReport(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
