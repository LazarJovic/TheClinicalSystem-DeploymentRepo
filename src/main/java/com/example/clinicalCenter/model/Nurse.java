package com.example.clinicalCenter.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("NURSE")
public class Nurse extends User {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Clinic clinic;

    public Nurse() {
        super();
    }

    public Nurse(String email, String password, String name, String surname, String phone) {
        super(email, password, name, surname, phone);
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
