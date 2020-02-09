package com.example.clinicalCenter.dto;

public class ExaminationTypeDTO {

    public Long id;

    public String name;

    public String price;

    public ExaminationTypeDTO() {
    }

    public ExaminationTypeDTO(Long id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
