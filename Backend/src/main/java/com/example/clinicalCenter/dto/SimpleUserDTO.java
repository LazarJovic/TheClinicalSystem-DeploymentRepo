package com.example.clinicalCenter.dto;

public class SimpleUserDTO {
    public String email;
    public String name;
    public String surname;

    public SimpleUserDTO(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
