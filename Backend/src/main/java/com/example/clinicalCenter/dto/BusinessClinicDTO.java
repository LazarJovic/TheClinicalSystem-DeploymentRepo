package com.example.clinicalCenter.dto;

public class BusinessClinicDTO {

    public Long id;
    public String name;
    public double rating;

    public BusinessClinicDTO() {
    }

    public BusinessClinicDTO(Long id, String name, double rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }
}
