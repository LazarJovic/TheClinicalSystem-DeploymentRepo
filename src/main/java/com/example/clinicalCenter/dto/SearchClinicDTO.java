package com.example.clinicalCenter.dto;

public class SearchClinicDTO {

    public String examinationDate;
    public String startTime;
    public String endTime;
    public String clinicName;
    public Long clinicId;
    public String examinationType;

    public SearchClinicDTO() {
    }

    public SearchClinicDTO(String examinationDate, String startTime, String endTime, String clinicName, Long clinicId, String examinationType) {
        this.examinationDate = examinationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clinicName = clinicName;
        this.clinicId = clinicId;
        this.examinationType = examinationType;
    }

}
