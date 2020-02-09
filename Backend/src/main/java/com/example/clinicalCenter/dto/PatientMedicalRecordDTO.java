package com.example.clinicalCenter.dto;

public class PatientMedicalRecordDTO {
    public Long id;
    public String email;
    public String name;
    public String surname;
    public Long medicalRecordId;

    public PatientMedicalRecordDTO() {
    }

    public PatientMedicalRecordDTO(Long id, String email, String name, String surname, Long medicalRecordId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.medicalRecordId = medicalRecordId;
    }
}
