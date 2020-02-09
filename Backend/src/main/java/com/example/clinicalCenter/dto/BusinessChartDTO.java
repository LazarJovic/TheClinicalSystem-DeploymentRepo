package com.example.clinicalCenter.dto;

import java.util.List;

public class BusinessChartDTO {

    public String name;
    public List<BusinessBarDTO> series;

    public BusinessChartDTO() {
    }

    public BusinessChartDTO(String name, List<BusinessBarDTO> series) {
        this.name = name;
        this.series = series;
    }

}
