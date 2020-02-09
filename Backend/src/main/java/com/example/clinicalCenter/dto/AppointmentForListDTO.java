package com.example.clinicalCenter.dto;

public class AppointmentForListDTO {

    public Long id;
    public String date;
    public String startTime;
    public String endTime;
    public Long doctorId;
    public String doctorName;
    public String doctorSurname;
    public Long nurseId;
    public Long roomId;
    public Long patientId;
    public String patientName;
    public String patientSurname;

    public AppointmentForListDTO() {
    }

    public AppointmentForListDTO(Long id, String date, String startTime, String endTime, Long doctorId, String doctorName,
                                 String doctorSurname, Long nurseId, Long roomId, Long patientId, String patientName,
                                 String patientSurname) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.nurseId = nurseId;
        this.roomId = roomId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientSurname = patientSurname;
    }

}


