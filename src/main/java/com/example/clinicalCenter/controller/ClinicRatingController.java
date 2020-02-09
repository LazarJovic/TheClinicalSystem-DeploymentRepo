package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.ClinicRatingDTO;
import com.example.clinicalCenter.service.ClinicRatingService;
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
@RequestMapping("/api/clinic-ratings")
public class ClinicRatingController {

    @Autowired
    private ClinicRatingService clinicRatingService;

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping(value = "/submit-rating-clinic")
    public ResponseEntity<?> submitClinicRating(@RequestBody ClinicRatingDTO dto) throws Exception {

        ClinicRatingDTO clinicRatingDTO = null;

        try {
            if (this.clinicRatingService.alreadyRated(dto)) {
                clinicRatingDTO = this.clinicRatingService.update(dto);
                return new ResponseEntity<>(clinicRatingDTO, HttpStatus.OK);
            } else {
                clinicRatingDTO = this.clinicRatingService.create(dto);
                return new ResponseEntity<>(clinicRatingDTO, HttpStatus.OK);
            }
        } catch (OptimisticLockException oe) {
            return new ResponseEntity<>("This clinic is in the rating process by another user. Please, try again later.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
