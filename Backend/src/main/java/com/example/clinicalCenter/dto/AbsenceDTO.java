package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.enums.AbsenceRequestStatus;
import com.example.clinicalCenter.model.enums.AbsenceType;

public class AbsenceDTO {

    public Long id;
    public String startDate;
    public String endDate;
    public AbsenceType type;
    public AbsenceRequestStatus status;
    public Long stuff_id;
    public String reasonStaff;
    public String reasonAdmin;

    public AbsenceDTO() {
    }

    public AbsenceDTO(Long id, String startDate, String endDate, Long stuff_id, AbsenceType type, AbsenceRequestStatus status, String reasonStaff, String reasonAdmin) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stuff_id = stuff_id;
        this.type = type;
        this.status = status;
        this.reasonStaff = reasonStaff;
        this.reasonAdmin = reasonAdmin;
    }
}
