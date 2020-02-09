package com.example.clinicalCenter.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("CLINIC_ADMIN")
public class ClinicAdmin extends User {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Clinic clinic;

    public ClinicAdmin() {
        super();
    }

    public ClinicAdmin(String email, String password, String name, String surname, String phone, Clinic clinic) {
        super(email, password, name, surname, phone);
        this.clinic = clinic;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
