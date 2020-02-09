package com.example.clinicalCenter.dto;

public class PatientEditDTO {
    public Long id;
    public String email;
    public String name;
    public String surname;
    public String address;
    public String city;
    public String country;
    public String phone;
    public String socialSecurityNumber;

    public PatientEditDTO() {
    }

    public PatientEditDTO(Long id, String email, String name, String surname, String address,
                          String city, String country, String phone, String socialSecurityNumber) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.socialSecurityNumber = socialSecurityNumber;
    }
}
