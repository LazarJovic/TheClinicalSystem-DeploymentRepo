package com.example.clinicalCenter.model;

import com.example.clinicalCenter.model.enums.AbsenceRequestStatus;
import com.example.clinicalCenter.model.enums.AbsenceType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "nurse_absence")
public class NurseAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nurse_id", referencedColumnName = "id")
    private Nurse nurse;

    private AbsenceType type;

    private AbsenceRequestStatus status;

    private String reasonStaff;

    private String reasonAdmin;

    public NurseAbsence() {
    }

    public NurseAbsence(Long id, LocalDate startDate, LocalDate endDate, AbsenceType type,
                        AbsenceRequestStatus status, String reasonStaff, String reasonAdmin) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.status = status;
        this.reasonStaff = reasonStaff;
        this.reasonAdmin = reasonAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Nurse getNurse() {
        return this.nurse;
    }

    public void setNurse(Nurse nurse) {
        this.nurse = nurse;
    }

    public AbsenceType getType() {
        return type;
    }

    public void setType(AbsenceType type) {
        this.type = type;
    }

    public AbsenceRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AbsenceRequestStatus status) {
        this.status = status;
    }

    public String getReasonStaff() {
        return reasonStaff;
    }

    public void setReasonStaff(String reasonStaff) {
        this.reasonStaff = reasonStaff;
    }

    public String getReasonAdmin() {
        return reasonAdmin;
    }

    public void setReasonAdmin(String reasonAdmin) {
        this.reasonAdmin = reasonAdmin;
    }
}
