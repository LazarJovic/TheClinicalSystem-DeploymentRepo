package com.example.clinicalCenter.dto;

public class DoctorRatingDTO {

    public Long id;
    public Long patientId;
    public Long doctorId;
    public int doctorRating;

    public DoctorRatingDTO() {
    }

    public DoctorRatingDTO(Long id, Long patientId, Long doctorId, int rating) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorRating = rating;
    }
}
