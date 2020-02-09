package com.example.clinicalCenter.model;

import javax.persistence.*;

@Entity
@Table(name = "operation_type")
public class OperationType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private double price;

    private boolean deleted;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Clinic clinic;

    @Version
    private Long version;

    public OperationType() {
    }

    public OperationType(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Clinic getClinic() {
        return this.clinic;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }
}
