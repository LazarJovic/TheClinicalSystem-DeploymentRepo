package com.example.clinicalCenter.model;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty", referencedColumnName = "id")
    private ExaminationType specialty;

    private Long ratingCount;

    private double ratingAvg;

    private LocalTime shiftStart;

    private LocalTime shiftEnd;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Clinic clinic;

    public Doctor() {
        super();
    }

    public Doctor(String email, String password, String name, String surname, String phone, Long ratingCount,
                  double ratingAvg, LocalTime shiftStart, LocalTime shiftEnd) {
        super(email, password, name, surname, phone);
        this.ratingCount = ratingCount;
        this.ratingAvg = ratingAvg;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
    }

    public ExaminationType getSpecialty() {
        return specialty;
    }

    public void setSpecialty(ExaminationType specialty) {
        this.specialty = specialty;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public LocalTime getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(LocalTime shiftStart) {
        this.shiftStart = shiftStart;
    }

    public LocalTime getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(LocalTime shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

}
