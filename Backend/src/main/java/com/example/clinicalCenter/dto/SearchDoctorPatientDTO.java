package com.example.clinicalCenter.dto;

public class SearchDoctorPatientDTO {

    public String searchName;
    public String searchSurname;
    public String searchRating;
    public String examinationDate;
    public String examinationStart;
    public String examinationEnd;
    public String clinicName;
    public Long clinicId;
    public String examinationType;

    public SearchDoctorPatientDTO() {
    }

    public SearchDoctorPatientDTO(String searchName, String searchSurname, String searchRating, String examinationDate,
                                  String examinationStart, String examinationEnd, String clinicName, Long clinicId, String examinationType) {
        this.searchName = searchName;
        this.searchSurname = searchSurname;
        this.searchRating = searchRating;
        this.examinationDate = examinationDate;
        this.examinationStart = examinationStart;
        this.examinationEnd = examinationEnd;
        this.clinicName = clinicName;
        this.clinicId = clinicId;
        this.examinationType = examinationType;
    }
}
