package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.AbsenceCalendarDTO;
import com.example.clinicalCenter.dto.AbsenceDTO;
import com.example.clinicalCenter.dto.AbsenceForListDTO;
import com.example.clinicalCenter.dto.CreateAbsenceDTO;
import com.example.clinicalCenter.exception.GenericConflictException;
import com.example.clinicalCenter.exception.NotFoundException;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.DoctorAbsenceMapper;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.DoctorAbsence;
import com.example.clinicalCenter.model.enums.AbsenceRequestStatus;
import com.example.clinicalCenter.repository.DoctorAbsenceRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorAbsenceService implements ServiceInterface<AbsenceDTO> {

    @Autowired
    private DoctorAbsenceRepository doctorAbsenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private EmailService emailService;

    private DoctorAbsenceMapper mapper = new DoctorAbsenceMapper();

    @Override
    public List<AbsenceDTO> findAll() {
        return null;
    }

    @Override
    public AbsenceDTO findOne(Long id) {
        return null;
    }

    public List<AbsenceDTO> findByLoggedIn() {
        Long staff_id = (userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName())).getId();
        List<AbsenceDTO> retVal = new ArrayList<>();
        for (DoctorAbsence a : this.doctorAbsenceRepository.findByStaff(staff_id)) {
            retVal.add(mapper.toDto(a));
        }
        return retVal;
    }

    public AbsenceDTO cancelAbsence(Long id) throws Exception {
        DoctorAbsence a = this.doctorAbsenceRepository.findById(id).get();
        if (a == null)
            throw new NotFoundException("Could not find absence!");
        a.setStatus(AbsenceRequestStatus.CANCELLED);
        return mapper.toDto(a);
    }

    public ArrayList<AbsenceForListDTO> findAllWaiting() {
        ArrayList<AbsenceForListDTO> retVal = new ArrayList<>();
        for (DoctorAbsence a : this.doctorAbsenceRepository.findAllWaiting()) {
            retVal.add(mapper.toAbsenceForListDto(a));
        }
        return retVal;
    }

    @Override
    public AbsenceDTO create(AbsenceDTO dto) throws Exception {
        return null;
    }

    public DoctorAbsence create(CreateAbsenceDTO dto, Long userId) {
        DoctorAbsence absence = mapper.toEntity(dto);
        absence.setDoctor((Doctor) this.userRepository.findById(userId).orElseGet(null));
        absence = this.doctorAbsenceRepository.save(absence);
        return absence;
    }

    public CreateAbsenceDTO sendAbsenceRequest(CreateAbsenceDTO dto) throws Exception {
        Long userId = (userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName())).getId();
        String validationString = this.validate(dto);
        if (!validationString.equals("OK"))
            throw new ValidationException(validationString);
        LocalDate startDate = LocalDate.parse(dto.startDate);
        LocalDate endDate = LocalDate.parse(dto.endDate);
        if (!examinationService.isDoctorAvailableForAbsence(userId, startDate, endDate))
            throw new GenericConflictException("You have an examination scheduled during the selected time period!");
        if (!this.hasUserAlreadySentAbsenceRequest(userId, startDate, endDate))
            throw new GenericConflictException("Conflicting absences! Change start/end date or cancel existing absence.");
        DoctorAbsence createdAbsence = this.create(dto, userId);

        return this.mapper.toCreateDto(createdAbsence);
    }

    @Override
    public AbsenceDTO update(AbsenceDTO dto) {
        return null;
    }

    @Override
    public AbsenceDTO delete(Long id) {
        return null;
    }

    public List<Long> stuffOnAbsence(LocalDate examDate) {
        List<DoctorAbsence> absences = this.doctorAbsenceRepository.findAll();
        List<Long> stuffOnAbsenceIds = new ArrayList<>();
        for (DoctorAbsence a : absences) {
            if (examDate.isAfter(a.getStartDate()) && examDate.isBefore(a.getEndDate())) {
                stuffOnAbsenceIds.add(a.getDoctor().getId());
            }
        }

        return stuffOnAbsenceIds;
    }

    public boolean hasUserAlreadySentAbsenceRequest(Long user_id, LocalDate startDate, LocalDate endDate) {
        List<DoctorAbsence> absences = this.doctorAbsenceRepository.findByStaff(user_id);
        for (DoctorAbsence a : absences) {
            if (isThereConflictBetweenTheseTwoDates(startDate, endDate, a.getStartDate(), a.getEndDate())) {
                return false;
            }
        }
        return true;
    }

    public boolean isThereConflictBetweenTheseTwoDates(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        return (isAfterOrEqual(startDate1, startDate2) && isBeforeOrEqual(startDate1, endDate2)
                || isAfterOrEqual(endDate1, startDate2) && isBeforeOrEqual(endDate1, endDate2))
                || (startDate1.isBefore(startDate2) && endDate1.isAfter(endDate2));
    }

    public boolean isBeforeOrEqual(LocalDate date1, LocalDate date2) {
        return (date1.isBefore(date2) || date1.isEqual(date2));
    }

    public boolean isAfterOrEqual(LocalDate date1, LocalDate date2) {
        return (date1.isAfter(date2) || date1.isEqual(date2));
    }

    public String validate(CreateAbsenceDTO dto) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            startDate = LocalDate.parse(dto.startDate);
            endDate = LocalDate.parse(dto.endDate);
        } catch (Exception e) {
            return "Could not parse selected dates!";
        }
        if (startDate.isEqual(endDate) || startDate.isAfter(endDate)) {
            return "Start date must be before end date!";
        }


        return "OK";
    }

    public List<AbsenceCalendarDTO> findApprovedAbsencesOfLoggedIn() throws Exception {
        Long staff_id = (userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName())).getId();
        List<AbsenceCalendarDTO> retVal = new ArrayList<>();
        for (DoctorAbsence a : this.doctorAbsenceRepository.findAcceptedByStaff(staff_id)) {
            LocalDate currDate = a.getStartDate();
            while (currDate.isBefore(a.getEndDate()) || currDate.isEqual(a.getEndDate())) {
                DoctorAbsence split_absence = new DoctorAbsence(a.getId(), currDate, currDate, a.getType(),
                        a.getStatus(), a.getReasonStaff(), a.getReasonAdmin());
                retVal.add(mapper.toCalendarDto(split_absence));
                currDate = currDate.plusDays(1);
            }
        }
        return retVal;
    }

    public AbsenceCalendarDTO findOneForCalendar(Long id) {
        return mapper.toCalendarDto(doctorAbsenceRepository.findById(id).get());
    }

    public AbsenceForListDTO acceptRequest(Long id) {
        DoctorAbsence request = this.doctorAbsenceRepository.findById(id).orElseGet(null);
        if (request != null && request.getStatus().equals(AbsenceRequestStatus.WAITING)) {
            request.setStatus(AbsenceRequestStatus.ACCEPTED);
            emailService.sendSimpleMessage(request.getDoctor().getEmail(),
                    "Your absence request has been accepted",
                    "Your absence request has been accepted by an administrator.");

            this.doctorAbsenceRepository.save(request);
            return mapper.toAbsenceForListDto(request);
        }
        return null;
    }

    public AbsenceForListDTO denyRequest(AbsenceForListDTO absence) {
        DoctorAbsence request = this.doctorAbsenceRepository.findById(absence.id).orElseGet(null);
        if (request != null && request.getStatus().equals(AbsenceRequestStatus.WAITING)) {
            request.setStatus(AbsenceRequestStatus.DENIED);
            request.setReasonAdmin(absence.reasonAdmin);
            emailService.sendSimpleMessage(request.getDoctor().getEmail(),
                    "Your absence request has been denied",
                    "Your absence request has been denied by an administrator. " +
                            "REASON: " + request.getReasonAdmin());

            this.doctorAbsenceRepository.save(request);
            return mapper.toAbsenceForListDto(request);
        }
        return null;
    }
}
