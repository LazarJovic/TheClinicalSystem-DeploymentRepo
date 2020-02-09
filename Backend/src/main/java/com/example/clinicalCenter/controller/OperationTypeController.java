package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.OperationTypeDTO;
import com.example.clinicalCenter.dto.SearchTypeDTO;
import com.example.clinicalCenter.model.OperationType;
import com.example.clinicalCenter.service.OperationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.List;

@RestController
@RequestMapping("/api/operationTypes")
public class OperationTypeController {

    @Autowired
    private OperationTypeService operationTypeService;

    @PreAuthorize("hasAnyRole('ROLE_CLINIC_ADMIN', 'ROLE_DOCTOR')")
    @GetMapping
    public ResponseEntity<List<OperationTypeDTO>> getAll() {
        List<OperationTypeDTO> examinationTypes = operationTypeService.findAll();

        return new ResponseEntity<>(examinationTypes, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeOperationType(@PathVariable Long id) {
        OperationTypeDTO operationType = null;
        try {
            operationType = this.operationTypeService.delete(id);
            return new ResponseEntity<>(operationType, HttpStatus.OK);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Cannot remove clinic profile at this moment.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OperationTypeDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OperationTypeDTO retVal = null;

        try {
            retVal = this.operationTypeService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateOperationType(@RequestBody OperationTypeDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OperationTypeDTO retVal = null;

        try {
            retVal = this.operationTypeService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (OptimisticLockException ole) {
            return new ResponseEntity<>("Cannot edit clinic profile at this moment.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping("/search-operation-types")
    public ResponseEntity<?> searchOperationTypes(@RequestBody SearchTypeDTO searchType) {
        if (searchType == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<OperationTypeDTO> retVal = null;

        try {
            retVal = this.operationTypeService.searchOperationTypes(searchType);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
