package com.example.clinicalCenter.dto;

public class CreateAbsenceDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public int type;
    public String reasonStaff;

    public CreateAbsenceDTO() {
    }

    public CreateAbsenceDTO(Long id, String startDate, String endDate, int type, String reasonStaff) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reasonStaff = reasonStaff;
    }
}
