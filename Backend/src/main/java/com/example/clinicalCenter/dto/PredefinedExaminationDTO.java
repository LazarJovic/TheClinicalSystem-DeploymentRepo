package com.example.clinicalCenter.dto;

public class PredefinedExaminationDTO {

    public Long id;
    public String typeName;
    public String examDate;
    public String startTime;
    public String endTime;
    public String priceWithDiscount;
    public String doctorName;
    public String nurseName;
    public String roomName;

    public PredefinedExaminationDTO() {
    }

    public PredefinedExaminationDTO(Long id, String typeName, String examDate, String startTime, String endTime,
                                    String priceWithDiscount, String doctorName, String nurseName, String roomName) {
        this.id = id;
        this.typeName = typeName;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priceWithDiscount = priceWithDiscount;
        this.doctorName = doctorName;
        this.nurseName = nurseName;
        this.roomName = roomName;
    }
}
