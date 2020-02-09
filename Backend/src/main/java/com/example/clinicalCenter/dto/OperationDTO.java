package com.example.clinicalCenter.dto;

import java.util.ArrayList;

public class OperationDTO {

    public Long id;
    public Long type;
    public String examDate;
    public String startTime;
    public String endTime;
    public ArrayList<Long> doctors;
    public Long room;
    public Long patient;

    public OperationDTO() {
    }

    public OperationDTO(Long id, Long type, String examDate, String startTime, String endTime, ArrayList<Long> doctors, Long room, Long patient) {
        this.id = id;
        this.type = type;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctors = doctors;
        this.room = room;
        this.patient = patient;
    }
}
