package com.example.clinicalCenter.dto;

import java.util.List;

public class DoctorRatingOperationDTO {

    public Long id;
    public Long operationId;
    public Long patientId;
    public List<Long> doctorIds;
    public int doctorRating;

    public DoctorRatingOperationDTO() {
    }

    public DoctorRatingOperationDTO(Long id, Long operationId, Long patientId, List<Long> doctorIds, int doctorRating) {
        this.id = id;
        this.operationId = operationId;
        this.patientId = patientId;
        this.doctorIds = doctorIds;
        this.doctorRating = doctorRating;
    }
}
