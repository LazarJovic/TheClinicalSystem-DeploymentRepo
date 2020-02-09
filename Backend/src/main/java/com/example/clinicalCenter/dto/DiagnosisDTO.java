package com.example.clinicalCenter.dto;

public class DiagnosisDTO {
    public Long id;
    public String name;
    public String code;

    public DiagnosisDTO() {
    }

    public DiagnosisDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
