package com.example.clinicalCenter.dto;

public class DrugDTO {
    public Long id;
    public String name;
    public String code;

    public DrugDTO() {
    }

    public DrugDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
