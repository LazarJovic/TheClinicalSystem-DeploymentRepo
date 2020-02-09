package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.RegisterRequestDTO;
import com.example.clinicalCenter.model.enums.RegisterRequestStatus;
import com.example.clinicalCenter.service.RegisterRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/requests")
public class RegisterRequestController {

    @Autowired
    private RegisterRequestService service;

    @PostMapping("/create")
    public ResponseEntity<RegisterRequestDTO> create(@RequestBody RegisterRequestDTO dto) {

        if (dto == null || dto.equals("")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        RegisterRequestDTO retVal = this.service.create(dto);
        if (retVal == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(retVal, HttpStatus.CREATED);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<RegisterRequestDTO> changeStatus(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        RegisterRequestDTO retVal = this.service.changeStatus(id);
        if (retVal == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<RegisterRequestDTO>> getRequests() {
        List<RegisterRequestDTO> requestsDTO = service.findAll();

        return new ResponseEntity<>(requestsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @GetMapping(value = "/status/{status}")
    public ResponseEntity<List<RegisterRequestDTO>> getRequestsByStatus(@PathVariable int status) {
        List<RegisterRequestDTO> requestsDTO;
        switch (status) {
            case 0:
                requestsDTO = service.findStatus(RegisterRequestStatus.WAITING_FOR_USER);
                break;
            case 1:
                requestsDTO = service.findStatus(RegisterRequestStatus.WAITING_FOR_ADMIN);
                break;
            case 2:
                requestsDTO = service.findStatus(RegisterRequestStatus.CONFIRMED);
                break;
            case 3:
                requestsDTO = service.findStatus(RegisterRequestStatus.DENIED);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(requestsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<RegisterRequestDTO> getRequest(@PathVariable Long id) {
        RegisterRequestDTO request = service.findOne(id);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PutMapping(value = "/confirm/{id}")
    public ResponseEntity<?> confirmRequest(@PathVariable Long id) {
        try {
            RegisterRequestDTO request = service.confirmRequest(id);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_CENTER_ADMIN')")
    @PutMapping(value = "/deny")
    public ResponseEntity<?> denyRequest(@RequestBody RegisterRequestDTO dto) {
        try {
            RegisterRequestDTO request = service.denyRequest(dto);
            return new ResponseEntity<>(request, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
