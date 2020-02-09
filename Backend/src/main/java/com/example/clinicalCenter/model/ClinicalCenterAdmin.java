package com.example.clinicalCenter.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CENTER_ADMIN")
public class ClinicalCenterAdmin extends User {

    public ClinicalCenterAdmin() {
        super();
    }

    public ClinicalCenterAdmin(String email, String password, String name, String surname, String phone) {
        super(email, password, name, surname, phone);
    }

}
