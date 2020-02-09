package com.example.clinicalCenter.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "examination")
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ExaminationType.class)
    @JoinColumn(name = "examination_type_id", referencedColumnName = "id")
    private ExaminationType type;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Doctor.class)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor doctor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nurse_id", referencedColumnName = "id")
    private Nurse nurse;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Clinic.class)
    private Clinic clinic;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    private Status status;

    private double discount;

    private boolean predefined;

    @Version
    private Long version;

    public Examination() {
        this.status = Status.AVAILABLE;
        this.predefined = false;
    }

    public Examination(LocalDateTime startDateTime, LocalDateTime endDateTime, ExaminationType type, Room room, Doctor doctor, Nurse nurse,
                       Patient patient, Status status, double discount) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.room = room;
        this.doctor = doctor;
        this.nurse = nurse;
        this.patient = patient;
        this.status = status;
        this.discount = discount;
        this.predefined = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setStartDateTime(LocalDateTime dateTime) {
        this.startDateTime = dateTime;
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public void setEndDateTime(LocalDateTime dateTime) {
        this.endDateTime = dateTime;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public void setType(ExaminationType type) {
        this.type = type;
    }

    public ExaminationType getType() {
        return type;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setNurse(Nurse nurse) {
        this.nurse = nurse;
    }

    public Nurse getNurse() {
        return this.nurse;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }

    public Long getVersion() {
        return version;
    }
}
