package com.example.clinicalCenter.dto;

public class OperationCalendarDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public String operatingRoomName;

    public OperationCalendarDTO(Long id, String startDate, String endDate, String operatingRoomName) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.operatingRoomName = operatingRoomName;
    }
}
