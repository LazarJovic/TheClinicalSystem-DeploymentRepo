package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.Status;

public class ExaminationCalendarDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public String roomName;
    public Status status;

    public ExaminationCalendarDTO() {
    }

    public ExaminationCalendarDTO(Long id, String startDate, String endDate, String roomName, Status status) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomName = roomName;
        this.status = status;
    }
}
