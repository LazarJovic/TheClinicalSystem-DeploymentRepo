package com.example.clinicalCenter.dto;

public class ClinicalCenterAdminDTO {

    public Long id;
    public String email;
    public String password;
    public String confirmPassword;
    public String name;
    public String surname;
    public String phone;

    public ClinicalCenterAdminDTO() {
    }

    public ClinicalCenterAdminDTO(Long id, String email, String password, String confirmedPassword, String name, String surname, String phone) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmedPassword;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }
}
