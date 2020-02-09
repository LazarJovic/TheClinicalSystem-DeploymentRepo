package com.example.clinicalCenter.dto;

public class ExaminationDTO {

    public Long id;
    public Long type;
    public String examDate;
    public String startTime;
    public String endTime;
    public String discount;
    public Long doctor;
    public Long nurse;
    public Long room;
    public Long patient;

    public ExaminationDTO() {
    }

    public ExaminationDTO(Long id, Long type, String examDate, String startTime, String endTime, String discount, Long doctor,
                          Long nurse, Long room, Long patient) {
        this.id = id;
        this.examDate = examDate;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.discount = discount;
        this.doctor = doctor;
        this.nurse = nurse;
        this.room = room;
        this.patient = patient;
    }
}
