package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.AbsenceCalendarDTO;
import com.example.clinicalCenter.dto.AbsenceDTO;
import com.example.clinicalCenter.dto.AbsenceForListDTO;
import com.example.clinicalCenter.dto.CreateAbsenceDTO;
import com.example.clinicalCenter.model.NurseAbsence;
import com.example.clinicalCenter.model.enums.AbsenceRequestStatus;
import com.example.clinicalCenter.model.enums.AbsenceType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NurseAbsenceMapper implements MapperInterface<NurseAbsence, AbsenceDTO> {

    @Override
    public NurseAbsence toEntity(AbsenceDTO dto) {
        return new NurseAbsence(dto.id, LocalDate.parse(dto.startDate), LocalDate.parse(dto.endDate), dto.type, dto.status, dto.reasonStaff,
                dto.reasonAdmin);
    }

    @Override
    public AbsenceDTO toDto(NurseAbsence entity) {
        return new AbsenceDTO(entity.getId(), entity.getStartDate().toString(), entity.getEndDate().toString(), entity.getNurse().getId(),
                entity.getType(), entity.getStatus(), entity.getReasonStaff(), entity.getReasonAdmin());
    }

    public AbsenceForListDTO toAbsenceForListDto(NurseAbsence entity) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String type = "";
        if (entity.getType() == AbsenceType.VACATION) {
            type = "Vacation";
        } else {
            type = "On leave";
        }
        return new AbsenceForListDTO(entity.getId(), entity.getStartDate().format(dateFormatter), entity.getEndDate().format(dateFormatter),
                type, entity.getReasonStaff(), "", entity.getNurse().getEmail());
    }

    public AbsenceCalendarDTO toCalendarDto(NurseAbsence entity) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return new AbsenceCalendarDTO(entity.getId(), entity.getStartDate().format(dateFormatter),
                entity.getEndDate().format(dateFormatter), entity.getType(), entity.getReasonStaff());
    }

    public CreateAbsenceDTO toCreateDto(NurseAbsence entity) {
        return new CreateAbsenceDTO(entity.getId(), entity.getStartDate().toString(), entity.getEndDate().toString(),
                entity.getType().ordinal(), entity.getReasonStaff());
    }

    public NurseAbsence toEntity(CreateAbsenceDTO dto) {
        AbsenceType type;
        if (dto.type == 0) {
            type = AbsenceType.VACATION;
        } else {
            type = AbsenceType.ON_LEAVE;
        }
        return new NurseAbsence(dto.id, LocalDate.parse(dto.startDate), LocalDate.parse(dto.endDate), type,
                AbsenceRequestStatus.WAITING, dto.reasonStaff, "");
    }
}
