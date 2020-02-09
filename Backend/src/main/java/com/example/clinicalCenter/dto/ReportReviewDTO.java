package com.example.clinicalCenter.dto;

import java.util.ArrayList;

public class ReportReviewDTO {
    public Long id;
    public String notes;
    public String diagnosis;
    public ArrayList<String> prescription;
    public boolean reviewed;
    public Long examination;
    public String patient;

    public ReportReviewDTO() {
    }

    public ReportReviewDTO(Long id, String notes, String diagnosis, ArrayList<String> prescription, boolean reviewed, Long examination, String patient) {
        this.id = id;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.reviewed = reviewed;
        this.examination = examination;
        this.patient = patient;
    }
}
