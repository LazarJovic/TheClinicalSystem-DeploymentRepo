package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.Status;

public class ExaminationCalendarDetailedDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public String roomName;
    public String patientName;
    public String patientSurname;
    public String patientEmail;
    public String nurseName;
    public String nurseSurname;
    public String nurseEmail;
    public String doctorName;
    public String doctorSurname;
    public String doctorEmail;
    public Status status;

    public ExaminationCalendarDetailedDTO(Long id, String startDate, String endDate, String roomName, String patientName, String patientSurname, String patientEmail, String nurseName, String nurseSurname, String nurseEmail, String doctorName, String doctorSurname, String doctorEmail, Status status) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomName = roomName;
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.patientEmail = patientEmail;
        this.nurseName = nurseName;
        this.nurseSurname = nurseSurname;
        this.nurseEmail = nurseEmail;
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.doctorEmail = doctorEmail;
        this.status = status;
    }
}
