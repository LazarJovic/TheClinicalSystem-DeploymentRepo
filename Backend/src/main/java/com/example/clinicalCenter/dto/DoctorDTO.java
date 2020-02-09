package com.example.clinicalCenter.dto;

public class DoctorDTO {

    public Long id;
    public String email;
    public String password;
    public String confirmPassword;
    public String name;
    public String surname;
    public String phone;
    public String shiftStart;
    public String shiftEnd;
    public Long specialty;

    public DoctorDTO() {
    }

    public DoctorDTO(Long id, String email, String password, String confirmPassword, String name, String surname, String phone, String shiftStart, String shiftEnd, Long specialty) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.specialty = specialty;
    }
}
