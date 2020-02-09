package com.example.clinicalCenter.dto;

public class SearchNurseDTO {

    public String searchName;
    public String searchSurname;
    public String searchPhone;

    public SearchNurseDTO() {
    }

    public SearchNurseDTO(String searchName, String searchSurname, String searchPhone) {
        this.searchName = searchName;
        this.searchSurname = searchSurname;
        this.searchPhone = searchPhone;
    }
}
