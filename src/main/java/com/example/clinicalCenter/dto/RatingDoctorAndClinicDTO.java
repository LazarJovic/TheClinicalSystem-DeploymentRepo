package com.example.clinicalCenter.dto;

public class RatingDoctorAndClinicDTO {

    public Long doctorId;
    public String doctorName;
    public String doctorSurname;
    public int doctorRate;
    public Long clinicId;
    public String clinicName;
    public String clinicCity;
    public String clinicAddress;
    public int clinicRate;
    public Long patientId;

    public RatingDoctorAndClinicDTO() {
    }

    public RatingDoctorAndClinicDTO(Long doctorId, String doctorName, String doctorSurname, int doctorRate, Long clinicId,
                                    String clinicName, String clinicCity, String clinicAddress, int clinicRate, Long patientId) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.doctorRate = doctorRate;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.clinicCity = clinicCity;
        this.clinicAddress = clinicAddress;
        this.clinicRate = clinicRate;
        this.patientId = patientId;
    }
}
