package com.example.clinicalCenter.dto;

public class UserDTO {
    public Long id;
    public String email;
    public String password;
    public String name;
    public String surname;
    public String address;
    public String city;
    public String country;
    public String phone;
    public String socialSecurityNumber;
    public String shiftStart;
    public String shiftEnd;
    public Long specialty;
    public Long clinic;
    public String confirmPassword;
    public String type;

    public UserDTO() {
    }

    //patient
    public UserDTO(Long id, String email, String password, String confirmPassword, String name, String surname, String address, String city, String country, String phone, String socialSecurityNumber) {
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

    //doctor
    public UserDTO(Long id, String email, String password, String confirmPassword, String name, String surname, String phone, String shiftStart, String shiftEnd, Long specialty) {
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

    //nurse i clinicCenterAdmin
    public UserDTO(Long id, String email, String password, String name, String surname, String phone) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    //clinicAdmin
    public UserDTO(Long id, String email, String password, String name, String surname, String phone, Long clinic) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.clinic = clinic;
    }
}
