package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.enums.BloodType;

public class MedicalRecordDTO {
    public Long id;
    public double height;
    public double weight;
    public BloodType bloodType;
    public String birthDate;
    public Long patient_id;

    public MedicalRecordDTO() {
    }

    public MedicalRecordDTO(Long id, double height, double weight, BloodType bloodType, String birthDate, Long patient_id) {
        this.id = id;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.birthDate = birthDate;
        this.patient_id = patient_id;
    }
}
