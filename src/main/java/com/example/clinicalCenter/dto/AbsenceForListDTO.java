package com.example.clinicalCenter.dto;

public class AbsenceForListDTO {
    public Long id;
    public String startDate;
    public String endDate;
    public String type;
    public String reasonStaff;
    public String reasonAdmin;
    public String email;

    public AbsenceForListDTO() {
    }

    public AbsenceForListDTO(Long id, String startDate, String endDate, String type, String reasonStaff, String reasonAdmin, String email) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.reasonStaff = reasonStaff;
        this.reasonAdmin = reasonAdmin;
        this.email = email;
    }
}
