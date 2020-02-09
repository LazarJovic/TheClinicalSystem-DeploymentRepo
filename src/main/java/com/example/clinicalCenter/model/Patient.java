package com.example.clinicalCenter.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends User {

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "socialSecurityNumber")
    private String socialSecurityNumber;

    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MedicalRecord record;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public MedicalRecord getRecord() {
        return record;
    }

    public void setRecord(MedicalRecord record) {
        this.record = record;
    }

    public Patient() {
        super();
    }

    public Patient(Long id, String email, String password, String name, String surname, String address, String city, String country, String phone, String socialSecurityNumber, MedicalRecord record) {
        super(email, password, name, surname, phone);
        this.address = address;
        this.city = city;
        this.country = country;
        this.socialSecurityNumber = socialSecurityNumber;
        this.record = record;
    }
}
