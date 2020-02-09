package com.example.clinicalCenter.dto;

public class CreateAppointmentDTO {

    public String date;
    public String startTime;
    public String endTime;
    public Long operationType;
    public Long patientId;

    public CreateAppointmentDTO() {
    }

    public CreateAppointmentDTO(String date, String startTime, String endTime, Long operationType, Long patientId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operationType = operationType;
        this.patientId = patientId;
    }

}
