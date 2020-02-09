package com.example.clinicalCenter.dto;

public class UserLoginDTO {

    public String username;
    public String password;
    public String role;

    public UserLoginDTO(String email, String password, String role) {
        this.username = email;
        this.password = password;
        this.role = role;
    }
}
