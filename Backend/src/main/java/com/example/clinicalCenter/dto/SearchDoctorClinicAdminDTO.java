package com.example.clinicalCenter.dto;

public class SearchDoctorClinicAdminDTO {

    public String searchName;
    public String searchSurname;
    public String searchPhone;
    public Long searchSpecialty;
    public String searchShiftStart;
    public String searchShiftEnd;

    public SearchDoctorClinicAdminDTO() {
    }

    public SearchDoctorClinicAdminDTO(String searchName, String searchSurname, String searchPhone, Long searchSpecialty,
                                      String searchShiftStart, String searchShiftEnd) {
        this.searchName = searchName;
        this.searchSurname = searchSurname;
        this.searchPhone = searchPhone;
        this.searchSpecialty = searchSpecialty;
        this.searchShiftStart = searchShiftStart;
        this.searchShiftEnd = searchShiftEnd;
    }
}
