package com.example.clinicalCenter.dto;

public class ExaminationParamsDTO {
    public Long type;
    public String examDate;
    public String startTime;
    public String endTime;

    public ExaminationParamsDTO() {
    }

    public ExaminationParamsDTO(Long type, String examDate, String startTime, String endTime) {
        this.type = type;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
