package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.ExaminationTypeDTO;
import com.example.clinicalCenter.dto.SearchTypeDTO;
import com.example.clinicalCenter.model.ExaminationType;
import com.example.clinicalCenter.service.ExaminationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.List;

@RestController
@RequestMapping("/api/examinationTypes")
public class ExaminationTypeController {

    @Autowired
    private ExaminationTypeService examinationTypeService;

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN', 'ROLE_PATIENT')")
    @GetMapping
    public ResponseEntity<List<ExaminationTypeDTO>> getAll() {
        List<ExaminationTypeDTO> examinationTypes = examinationTypeService.findAll();

        return new ResponseEntity<>(examinationTypes, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/get-all")
    public ResponseEntity<List<ExaminationTypeDTO>> getAllForPatient() {
        List<ExaminationTypeDTO> examinationTypes = examinationTypeService.getAllExaminationTypes();

        return new ResponseEntity<>(examinationTypes, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ExaminationTypeDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ExaminationTypeDTO retVal = null;

        try {
            retVal = this.examinationTypeService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeExaminationType(@PathVariable Long id) {
        ExaminationTypeDTO examinationType = null;
        try {
            examinationType = this.examinationTypeService.delete(id);
            return new ResponseEntity<>(examinationType, HttpStatus.OK);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Cannot edit examination type at this moment.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateExaminationType(@RequestBody ExaminationTypeDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ExaminationTypeDTO retVal = null;

        try {
            retVal = this.examinationTypeService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Examination type has already been removed by another admin.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-examination-types")
    public ResponseEntity<?> searchExaminationTypes(@RequestBody SearchTypeDTO searchType) {
        if (searchType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ExaminationTypeDTO> retVal = null;

        try {
            retVal = this.examinationTypeService.searchExaminationTypes(searchType);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
