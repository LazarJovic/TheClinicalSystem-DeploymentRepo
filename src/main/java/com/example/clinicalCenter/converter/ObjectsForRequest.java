package com.example.clinicalCenter.converter;

import com.example.clinicalCenter.dto.RoomTimeDTO;
import com.example.clinicalCenter.model.Doctor;

public class ObjectsForRequest {

    private RoomTimeDTO roomTimeDTO;
    private Doctor doctor;

    public ObjectsForRequest() {
    }

    public ObjectsForRequest(RoomTimeDTO roomTimeDTO, Doctor doctor) {
        this.roomTimeDTO = roomTimeDTO;
        this.doctor = doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setRoomTimeDTO(RoomTimeDTO roomTimeDTO) {
        this.roomTimeDTO = roomTimeDTO;
    }

    public RoomTimeDTO getRoomTimeDTO() {
        return this.roomTimeDTO;
    }

}
