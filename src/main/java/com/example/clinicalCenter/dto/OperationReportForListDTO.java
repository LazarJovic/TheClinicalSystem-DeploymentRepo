package com.example.clinicalCenter.dto;

public class OperationReportForListDTO {

    public Long reportId;
    public String date;
    public String time;
    public String diagnosis;
    public String doctorsNameSurname;

    public OperationReportForListDTO() {
    }

    public OperationReportForListDTO(Long reportId, String date, String time, String diagnosis, String doctorsNameSurname) {
        this.reportId = reportId;
        this.date = date;
        this.time = time;
        this.diagnosis = diagnosis;
        this.doctorsNameSurname = doctorsNameSurname;
    }
}
