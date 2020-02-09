package com.example.clinicalCenter.dto;

public class RoomDTO {

    public Long id;
    public String name;
    public String type;

    public RoomDTO() {
    }

    public RoomDTO(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
