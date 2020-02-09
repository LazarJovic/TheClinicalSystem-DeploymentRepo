package com.example.clinicalCenter.dto;

public class OperationTypeDTO {

    public Long id;

    public String name;

    public String price;

    public OperationTypeDTO() {
    }

    public OperationTypeDTO(Long id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
