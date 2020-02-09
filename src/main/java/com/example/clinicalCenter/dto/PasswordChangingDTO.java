package com.example.clinicalCenter.dto;

public class PasswordChangingDTO {
    public String oldPassword;
    public String newPassword;

    public PasswordChangingDTO() {
    }

    public PasswordChangingDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
