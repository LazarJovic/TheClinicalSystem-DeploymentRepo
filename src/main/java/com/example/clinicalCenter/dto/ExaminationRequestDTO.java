package com.example.clinicalCenter.dto;

public class ExaminationRequestDTO {

    public Long id;
    public String examDate;
    public String startTime;
    public String endTime;
    public Long type;
    public Long doctor;
    public Long clinic;
    public Long room;
    public Long patient;

    public ExaminationRequestDTO() {
    }

    public ExaminationRequestDTO(Long id, String examDate, String startTime, String endTime, Long type, Long doctor,
                                 Long clinic, Long room, Long patient) {
        this.id = id;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.doctor = doctor;
        this.clinic = clinic;
        this.room = room;
        this.patient = patient;
    }
}
