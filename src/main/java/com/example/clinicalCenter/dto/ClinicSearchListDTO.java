package com.example.clinicalCenter.dto;

public class ClinicSearchListDTO {

    public Long id;
    public String name;
    public String address;
    public String city;
    public String rating;
    public String examinationPrice;

    public ClinicSearchListDTO() {
    }

    public ClinicSearchListDTO(Long id, String name, String address, String city, String rating, String examinationPrice) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.rating = rating;
        this.examinationPrice = examinationPrice;
    }
}
