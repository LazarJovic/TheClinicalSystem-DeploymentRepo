package com.example.clinicalCenter.dto;

public class OperationForListDTO {

    public Long id;
    public String typeName;
    public String operDate;
    public String startTime;
    public String endTime;
    public String price;
    public String allDoctorsNames;
    public String roomName;

    public OperationForListDTO() {

    }

    public OperationForListDTO(Long id, String typeName, String operDate, String startTime, String endTime,
                               String price, String allDoctorsNames, String roomName) {
        this.id = id;
        this.typeName = typeName;
        this.operDate = operDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.allDoctorsNames = allDoctorsNames;
        this.roomName = roomName;
    }
}
