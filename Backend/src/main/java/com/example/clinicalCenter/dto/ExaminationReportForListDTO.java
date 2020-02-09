package com.example.clinicalCenter.dto;

public class ExaminationReportForListDTO {

    public Long reportId;
    public String date;
    public String time;
    public String diagnosis;
    public String doctorNameSurname;

    public ExaminationReportForListDTO() {
    }

    public ExaminationReportForListDTO(Long reportId, String date, String time, String diagnosis, String doctorNameSurname) {
        this.reportId = reportId;
        this.date = date;
        this.time = time;
        this.diagnosis = diagnosis;
        this.doctorNameSurname = doctorNameSurname;
    }
}
