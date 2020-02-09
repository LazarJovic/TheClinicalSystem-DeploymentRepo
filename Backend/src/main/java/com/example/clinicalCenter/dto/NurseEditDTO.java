package com.example.clinicalCenter.dto;

public class NurseEditDTO {

    public Long id;
    public String email;
    public String name;
    public String surname;
    public String phone;

    public NurseEditDTO() {
    }

    public NurseEditDTO(Long id, String email, String name, String surname, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }
}
