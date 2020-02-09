package com.example.clinicalCenter.dto;

public class SearchTypeDTO {
    public String searchName;
    public String minPrice;
    public String maxPrice;

    public SearchTypeDTO() {
    }

    public SearchTypeDTO(String searchName, String minPrice, String maxPrice) {
        this.searchName = searchName;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
