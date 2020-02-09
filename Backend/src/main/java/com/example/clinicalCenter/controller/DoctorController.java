package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.service.CustomUserDetailsService;
import com.example.clinicalCenter.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getDoctorForEdit(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DoctorEditDTO dto = this.doctorService.findOneForEdit(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<?> addUser(@RequestBody DoctorDTO dto) {
        if (userDetailsService.emailTaken(dto.email)) {
            return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
        }

        DoctorDTO retVal = null;

        try {
            retVal = this.doctorService.create(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorEditDTO dto) {
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DoctorEditDTO retVal = null;

        try {
            retVal = this.doctorService.update(dto);
            return new ResponseEntity<>(retVal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping(value = "/doctors-of-clinic")
    public ResponseEntity<?> getDoctorsOfClinic() {
        ArrayList<DoctorForListDTO> doctorsOfClinic = null;

        try {
            doctorsOfClinic = this.doctorService.getDoctorsOfClinic();
            return new ResponseEntity<>(doctorsOfClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_CLINIC_ADMIN')")
    @GetMapping(value = "/clinic-doctors/{clinicId}")
    public ResponseEntity<?> getDoctorsOfClinic(@PathVariable Long clinicId) {
        ArrayList<DoctorSearchListDTO> doctorsOfClinic = null;

        try {
            doctorsOfClinic = this.doctorService.getDoctorsOfClinic(clinicId);
            return new ResponseEntity<>(doctorsOfClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeDoctor(@PathVariable Long id) {
        DoctorDTO doctor = null;
        try {
            doctor = this.doctorService.delete(id);
            return new ResponseEntity<>(doctor, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/get-free-doctors")
    public ResponseEntity<?> allFreeDoctors(@RequestBody ExaminationParamsDTO examinationParams) {
        List<DoctorForListDTO> freeDoctors = null;
        try {
            freeDoctors = this.doctorService.getFreeDoctors(examinationParams);
            return new ResponseEntity<>(freeDoctors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/search-doctors-ca")
    public ResponseEntity<?> searchDoctorsClinicAdmin(@RequestBody SearchDoctorClinicAdminDTO searchDoctor) {
        if (searchDoctor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<DoctorForListDTO> retVal = null;

        try {
            retVal = this.doctorService.searchDoctorsCA(searchDoctor);
            return new ResponseEntity<>(retVal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @GetMapping("/doctors-rate")
    public ResponseEntity<?> getDoctorsRates() {
        return new ResponseEntity<>(this.doctorService.getDoctorsRates(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_CLINIC_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getClinic(@PathVariable Long id) {
        DoctorDTO request = this.doctorService.findOne(id);
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_CLINIC_ADMIN')")
    @PostMapping(value = "/get-free-doctors-request")
    public ResponseEntity<?> allFreeDoctorsForRequest(@RequestBody RoomTimeDTO roomTime) {
        List<DoctorForListDTO> freeDoctors = null;
        try {
            freeDoctors = this.doctorService.getFreeDoctorsForRequest(roomTime, false);
            return new ResponseEntity<>(freeDoctors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
