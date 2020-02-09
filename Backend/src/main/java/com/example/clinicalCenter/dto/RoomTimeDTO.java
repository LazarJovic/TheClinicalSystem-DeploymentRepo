package com.example.clinicalCenter.dto;

public class RoomTimeDTO {

    public Long id;
    public String name;
    public String type;
    public String date;
    public String startTime;
    public String endTime;
    public boolean isDoctorAvailable;

    public RoomTimeDTO() {
    }

    public RoomTimeDTO(Long id, String name, String type, String date, String startTime, String endTime, boolean isDoctorAvailable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDoctorAvailable = isDoctorAvailable;
    }

}
