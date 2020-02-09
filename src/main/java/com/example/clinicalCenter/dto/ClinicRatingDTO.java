package com.example.clinicalCenter.dto;

public class ClinicRatingDTO {

    public Long id;
    public Long patientId;
    public Long clinicId;
    public int clinicRating;

    public ClinicRatingDTO() {
    }

    public ClinicRatingDTO(Long id, Long patientId, Long clinicId, int clinicRating) {
        this.id = id;
        this.patientId = patientId;
        this.clinicId = clinicId;
        this.clinicRating = clinicRating;
    }
}
