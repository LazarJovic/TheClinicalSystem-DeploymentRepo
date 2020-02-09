package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.Status;

import java.util.List;

public class OperationCalendarDetailedDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public String roomName;
    public String patientName;
    public String patientSurname;
    public String patientEmail;
    public List<String> doctorNames;
    public List<String> doctorSurnames;
    public List<String> doctorEmails;
    public Status status;

    public OperationCalendarDetailedDTO() {
    }

    public OperationCalendarDetailedDTO(Long id, String startDate, String endDate, String roomName, String patientName, String patientSurname, String patientEmail, List<String> doctorNames, List<String> doctorSurnames, List<String> doctorEmails, Status status) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomName = roomName;
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.patientEmail = patientEmail;
        this.doctorNames = doctorNames;
        this.doctorSurnames = doctorSurnames;
        this.doctorEmails = doctorEmails;
        this.status = status;
    }
}
