package com.example.clinicalCenter.dto;

public class DoctorForListDTO {
    public Long id;
    public String email;
    public String name;
    public String surname;
    public String phone;
    public String shiftStart;
    public String shiftEnd;

    public DoctorForListDTO() {
    }

    public DoctorForListDTO(Long id, String email, String name, String surname, String phone, String shiftStart, String shiftEnd) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
    }
}
