package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.enums.RegisterRequestStatus;

public class RegisterRequestDTO {

    public Long id;
    public String email;
    public String password;
    public String verifiedPassword;
    public String name;
    public String surname;
    public String address;
    public String city;
    public String country;
    public String phone;
    public String socialSecurityNumber;
    public RegisterRequestStatus status;
    public String reason;

    public RegisterRequestDTO(String email, String password, String verifiedPassword, String name,
                              String surname, String address, String city, String country, String phone,
                              String socialSecurityNumber, RegisterRequestStatus status, String reason) {
        this.email = email;
        this.password = password;
        this.verifiedPassword = verifiedPassword;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phone = phone;
        this.socialSecurityNumber = socialSecurityNumber;
        if (status == null) {
            this.status = RegisterRequestStatus.WAITING_FOR_USER;
        } else {
            this.status = status;
        }
        this.reason = reason;
    }

    public RegisterRequestDTO() {
    }

}
