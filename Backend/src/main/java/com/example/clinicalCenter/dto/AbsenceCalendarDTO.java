package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.enums.AbsenceType;

public class AbsenceCalendarDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public AbsenceType type;
    public String reason;

    public AbsenceCalendarDTO() {
    }

    public AbsenceCalendarDTO(Long id, String startDate, String endDate, AbsenceType type, String reason) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reason = reason;
    }
}
