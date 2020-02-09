package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.DoctorRatingDTO;
import com.example.clinicalCenter.dto.DoctorRatingOperationDTO;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.service.DoctorRatingService;
import com.example.clinicalCenter.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.OptimisticLockException;

@RestController
@RequestMapping("/api/doctor-ratings")
public class DoctorRatingController {

    @Autowired
    private DoctorRatingService doctorRatingService;

    @Autowired
    private DoctorService doctorService;

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/submit-rating-doctor-examination")
    public ResponseEntity<?> submitDoctorRatingExamination(@RequestBody DoctorRatingDTO dto) throws Exception {

        DoctorRatingDTO doctorRatingDTO = null;

        try {
            if (this.doctorRatingService.alreadyRated(dto)) {
                doctorRatingDTO = this.doctorRatingService.update(dto);
                return new ResponseEntity<>(doctorRatingDTO, HttpStatus.OK);
            } else {
                doctorRatingDTO = this.doctorRatingService.create(dto);
                return new ResponseEntity<>(doctorRatingDTO, HttpStatus.OK);
            }
        } catch (OptimisticLockException oe) {
            return new ResponseEntity<>("This doctor is in the rating process by another user. Please, try again later.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/submit-rating-doctor-operation")
    public ResponseEntity<?> submitDoctorRatingOperation(@RequestBody DoctorRatingOperationDTO dto) throws Exception {

        DoctorRatingOperationDTO doctorRatingDTO = null;

        try {
            for (Long id : dto.doctorIds) {
                Doctor d = this.doctorService.findOneEntity(id);
                if (this.doctorRatingService.alreadyRatedForOperation(d.getId(), dto.patientId)) {
                    doctorRatingDTO = this.doctorRatingService.updateForOperation(dto, id);
                } else {
                    doctorRatingDTO = this.doctorRatingService.createForOperation(dto, id);
                }
            }
            return new ResponseEntity<>(doctorRatingDTO, HttpStatus.OK);
        } catch (OptimisticLockException oe) {
            return new ResponseEntity<>("This doctor is in the rating process by another user. Please, try again later.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
