package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.RegisterRequest;

import java.util.Objects;

public class PatientDTO {
    public Long id;
    public String email;
    public String password;
    public String confirmPassword;
    public String name;
    public String surname;
    public String address;
    public String city;
    public String country;
    public String phone;
    public String socialSecurityNumber;

    public PatientDTO() {
    }

    public PatientDTO(RegisterRequest request) {
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.confirmPassword = request.getPassword();
        this.name = request.getName();
        this.surname = request.getSurname();
        this.address = request.getAddress();
        this.city = request.getCity();
        this.country = request.getCountry();
        this.phone = request.getPhone();
        this.socialSecurityNumber = request.getSocialSecurityNumber();
    }

    public PatientDTO(Long id, String email, String password, String confirmPassword, String name, String surname, String address,
                      String city, String country, String phone, String socialSecurityNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientDTO that = (PatientDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(address, that.address) &&
                Objects.equals(city, that.city) &&
                Objects.equals(country, that.country) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(socialSecurityNumber, that.socialSecurityNumber);
    }
}
