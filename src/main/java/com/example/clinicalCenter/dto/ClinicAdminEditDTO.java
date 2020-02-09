package com.example.clinicalCenter.dto;

public class ClinicAdminEditDTO {

    public Long id;
    public String email;
    public String name;
    public String surname;
    public String phone;

    public ClinicAdminEditDTO() {
    }

    public ClinicAdminEditDTO(Long id, String email, String name, String surname, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }
}
