package com.example.clinicalCenter.dto;

public class ClinicDTO {

    public Long id;
    public String name;
    public String address;
    public String city;
    public String description;

    public ClinicDTO() {
    }

    public ClinicDTO(Long id, String name, String address, String city, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.description = description;
    }
}
