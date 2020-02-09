package com.example.clinicalCenter.dto;

import com.example.clinicalCenter.model.Drug;
import com.example.clinicalCenter.model.Report;

import java.util.ArrayList;

public class ReportDTO {
    public Long id;
    public String notes;
    public Long diagnosis;
    public ArrayList<Long> prescription;
    public boolean reviewed;
    public Long examination;

    public ReportDTO(Report r) {
        this.id = r.getId();
        this.notes = r.getNotes();
        this.diagnosis = r.getDiagnosis().getId();
        this.prescription = new ArrayList<Long>();
        for (Drug d : r.getPrescription())
            this.prescription.add(d.getId());
        this.reviewed = r.isReviewed();
        this.examination = r.getExamination().getId();
    }

    public ReportDTO() {
    }

    public ReportDTO(Long id, String notes, Long diagnosis, ArrayList<Long> prescription, boolean reviewed, Long examination) {
        this.id = id;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.reviewed = reviewed;
        this.examination = examination;
    }
}
