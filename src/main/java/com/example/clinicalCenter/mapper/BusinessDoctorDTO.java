package com.example.clinicalCenter.mapper;

public class BusinessDoctorDTO {
    public Long id;
    public String email;
    public String name;
    public String surname;
    public double rating;

    public BusinessDoctorDTO() {
    }

    public BusinessDoctorDTO(Long id, String email, String name, String surname, double rating) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.rating = rating;
    }
}
