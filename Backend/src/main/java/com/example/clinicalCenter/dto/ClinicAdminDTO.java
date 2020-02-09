package com.example.clinicalCenter.dto;

public class ClinicAdminDTO {

    public Long id;
    public String email;
    public String password;
    public String confirmPassword;
    public String name;
    public String surname;
    public String phone;
    public long clinic;

    public ClinicAdminDTO() {
    }

    public ClinicAdminDTO(Long id, String email, String password, String confirmPassword, String name, String surname, String phone, Long clinic) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.clinic = clinic;
    }
}
