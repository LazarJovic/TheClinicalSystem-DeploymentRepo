package com.example.clinicalCenter.dto;

public class DoctorSearchListDTO {

    public Long id;
    public String name;
    public String surname;
    public String rating;
    public String examinationDate;
    public String examinationStart;
    public String examinationEnd;

    public DoctorSearchListDTO() {
    }

    public DoctorSearchListDTO(Long id, String name, String surname, String rating, String examinationDate, String examinationStart, String examinationEnd) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.rating = rating;
        this.examinationDate = examinationDate;
        this.examinationStart = examinationStart;
        this.examinationEnd = examinationEnd;
    }
}
