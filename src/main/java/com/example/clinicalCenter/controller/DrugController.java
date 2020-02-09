package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.DrugDTO;
import com.example.clinicalCenter.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {
    @Autowired
    private DrugService drugService;

    @PreAuthorize("hasAnyRole('ROLE_CENTER_ADMIN', 'ROLE_DOCTOR')")
    @GetMapping()
    public ResponseEntity<?> getDiagnoses() {
        List<DrugDTO> list = drugService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody DrugDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DrugDTO retVal = null;

        try {
            retVal = this.drugService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
