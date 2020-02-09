package com.example.clinicalCenter.dto;

public class AppointmentDTO {

    public Long id;
    public String type;
    public String examDate;
    public String startTime;
    public String endTime;
    public String doctorName;
    public String doctorSurname;
    public Long nurseId;
    public String roomName;
    public Long patientId;
    public String patientName;
    public String patientSurname;
    public boolean isFromList;

    public AppointmentDTO() {
    }

    public AppointmentDTO(Long id, String type, String examDate, String startTime, String endTime, String doctorName,
                          String doctorSurname, Long nurseId, String roomName, Long patientId, String patientName,
                          String patientSurname) {
        this.id = id;
        this.examDate = examDate;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.nurseId = nurseId;
        this.roomName = roomName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.isFromList = true;
    }

}
