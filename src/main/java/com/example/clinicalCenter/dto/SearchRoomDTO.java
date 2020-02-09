package com.example.clinicalCenter.dto;

public class SearchRoomDTO {

    public String searchName;
    public String searchType;

    public SearchRoomDTO() {
    }

    public SearchRoomDTO(String searchName, String searchType) {
        this.searchName = searchName;
        this.searchType = searchType;
    }
}
