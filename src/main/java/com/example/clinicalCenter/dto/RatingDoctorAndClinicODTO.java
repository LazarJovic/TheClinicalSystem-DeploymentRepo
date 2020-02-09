package com.example.clinicalCenter.dto;

import java.util.List;

public class RatingDoctorAndClinicODTO {

    public Long operationId;
    public List<Long> doctorIds;
    public String doctorNamesSurnames;
    public int doctorRate;
    public Long clinicId;
    public String clinicName;
    public String clinicCity;
    public String clinicAddress;
    public int clinicRate;
    public Long patientId;

    public RatingDoctorAndClinicODTO() {
    }

    public RatingDoctorAndClinicODTO(Long operationId, List<Long> doctorId, String doctorNamesSurnames, int doctorRate, Long clinicId,
                                     String clinicName, String clinicCity, String clinicAddress, int clinicRate, Long patientId) {
        this.operationId = operationId;
        this.doctorIds = doctorId;
        this.doctorNamesSurnames = doctorNamesSurnames;
        this.doctorRate = doctorRate;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.clinicCity = clinicCity;
        this.clinicAddress = clinicAddress;
        this.clinicRate = clinicRate;
        this.patientId = patientId;
    }

}
