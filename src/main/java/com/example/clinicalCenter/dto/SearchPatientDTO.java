package com.example.clinicalCenter.dto;

public class SearchPatientDTO {
    public String searchName;
    public String searchSurname;
    public String searchPhone;
    public String searchAddress;
    public String searchCity;
    public String searchSocialNumber;

    public SearchPatientDTO() {
    }

    public SearchPatientDTO(String searchName, String searchSurname, String searchPhone, String searchAddress,
                            String searchCity, String searchSocialNumber) {
        this.searchName = searchName;
        this.searchSurname = searchSurname;
        this.searchPhone = searchPhone;
        this.searchAddress = searchAddress;
        this.searchCity = searchCity;
        this.searchSocialNumber = searchSocialNumber;
    }
}
