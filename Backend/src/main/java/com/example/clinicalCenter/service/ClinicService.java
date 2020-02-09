package com.example.clinicalCenter.service;


import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.ClinicMapper;
import com.example.clinicalCenter.mapper.DoctorMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClinicService implements ServiceInterface<ClinicDTO> {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExaminationTypeRepository examinationTypeRepository;

    @Autowired
    private DoctorAbsenceRepository doctorAbsenceRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private OperationRepository operationRepository;

    private ClinicMapper clinicMapper;
    private DoctorMapper doctorMapper;

    public ClinicService() {

        this.clinicMapper = new ClinicMapper();
        this.doctorMapper = new DoctorMapper();
    }

    @Override
    public List<ClinicDTO> findAll() {
        List<ClinicDTO> clinics = new ArrayList<ClinicDTO>();
        for (Clinic c : findAllEntity()) {
            clinics.add(clinicMapper.toDto(c));
        }
        return clinics;
    }

    public List<ClinicSearchListDTO> findAllForSearchList() {
        List<ClinicSearchListDTO> clinics = new ArrayList<ClinicSearchListDTO>();
        for (Clinic c : findAllEntity()) {
            clinics.add(this.clinicMapper.toDtoSearchList(c, null));
        }
        return clinics;
    }

    public List<Clinic> findAllEntity() {
        try {
            List<Clinic> list = clinicRepository.findAll();
            return list;
        } catch (Exception e) {
            //System.out.println("Usao");
        }

        return null;

    }

    @Override
    public ClinicDTO findOne(Long id) {
        return clinicMapper.toDto(clinicRepository.findById(id).get());
    }

    public Clinic findOneEntity(Long id) {
        return clinicRepository.findById(id).get();
    }

    @Override
    @Transactional(readOnly = false)
    public ClinicDTO create(ClinicDTO dto) {
        if (dto == null || dto.name.equals("") || dto.address.equals("") || dto.city.equals(""))
            return null;
        Clinic c = clinicRepository.save(clinicMapper.toEntity(dto));
        return clinicMapper.toDto(c);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public ClinicDTO update(ClinicDTO dto) throws Exception {
        if (dto == null || dto.name.equals("") || dto.address.equals("") || dto.city.equals("")) {
            String validation = "Fields are not filled correctly!";
            throw new ValidationException(validation);
        }
        Clinic updatedClinic = this.clinicRepository.findById(dto.id).orElseGet(null);
        updatedClinic.setName(dto.name);
        updatedClinic.setAddress(dto.address);
        updatedClinic.setCity(dto.city);
        updatedClinic.setDescription(dto.description);

        Clinic room = this.clinicRepository.save(updatedClinic);
        return this.clinicMapper.toDto(room);
    }

    @Override
    public ClinicDTO delete(Long id) {
        ClinicDTO dto = findOne(id);
        clinicRepository.deleteById(id);
        return dto;
    }

    public ClinicDTO getCurrentClinic() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }

        Clinic currentClinic = this.clinicRepository.findById(clinicId).orElseGet(null);
        return this.clinicMapper.toDto(currentClinic);
    }


    public BusinessClinicDTO getClinicRate() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Clinic clinic = null;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinic = clinicAdmin.getClinic();
        }

        return this.clinicMapper.toBusinessClinicDTO(clinic);
    }

    public List<DoctorSearchListDTO> getDoctorsOfClinicForSearchList(List<Doctor> entities, String date, String satrtTime, String endTime) {
        List<DoctorSearchListDTO> retVal = new ArrayList<DoctorSearchListDTO>();
        for (Doctor d : entities) {
            retVal.add(this.doctorMapper.toDoctorSearchListDto(d, date, satrtTime, endTime));
        }
        return retVal;
    }

    public List<ClinicSearchListDTO> searchClinic(SearchClinicDTO searchClinicDTO) throws Exception {

        String validation = dtoSearchValidation(searchClinicDTO);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        List<Clinic> allClinics = this.clinicRepository.findAll();
        Iterator i = allClinics.iterator();
        while (i.hasNext()) {
            Clinic c = (Clinic) i.next();
            // 1. Does doctor have a specialty of give examination type?
            List<Doctor> doctors = this.getValidDostors(c.getId(), searchClinicDTO);
            if (doctors.isEmpty()) {
                i.remove();
                continue;
            }
            if (isThereName(searchClinicDTO.clinicName)) {
                if (!containsName(c, searchClinicDTO.clinicName)) {
                    i.remove();
                }
            }
        }
        return this.toClinicDTOSearchList(allClinics, searchClinicDTO.examinationType);
    }

    public List<Doctor> getValidDostors(Long clinicId, SearchClinicDTO searchClinicDTO) {
        List<Doctor> allDoctors = null;
        if (isThereType(searchClinicDTO.examinationType)) {
            allDoctors = this.userRepository.findDoctorsOfClinicBySpecialty(clinicId, this.getExaminationTypeId(searchClinicDTO.examinationType));
        } else {
            allDoctors = this.userRepository.findDoctorsOfClinic(clinicId);
        }
        List<Doctor> doctorsOfClinic = new ArrayList<Doctor>();
        for (Doctor d : allDoctors) {

            if (isThereDate(searchClinicDTO.examinationDate)) {
                // 2. Is doctor on holiday on that day?
                if (doctorHasAbsence(d, searchClinicDTO.examinationDate)) {
                    continue;
                }
            }

            // 3. Is an examination in doctor's shift?
            if (!isTimeInDoctorsShift(d, searchClinicDTO.startTime, searchClinicDTO.endTime)) {
                continue;
            }

            // 4. Can doctor make a wanted examination on that day?
            if (!doesDoctorHaveTime(d, searchClinicDTO.startTime, searchClinicDTO.endTime, searchClinicDTO.examinationDate)) {
                continue;
            }

            doctorsOfClinic.add(d);
        }

        return doctorsOfClinic;
    }

    private Long getExaminationTypeId(String name) {
        List<ExaminationType> types = this.examinationTypeRepository.findAll();
        for (ExaminationType et : types) {
            if (et.getName().equals(name)) {
                return et.getId();
            }
        }
        return null;
    }

    private ExaminationType getExaminationType(String name) {
        List<ExaminationType> types = this.examinationTypeRepository.findAll();
        for (ExaminationType et : types) {
            if (et.getName().equals(name)) {
                return et;
            }
        }
        return null;
    }

    private List<ClinicSearchListDTO> toClinicDTOSearchList(List<Clinic> entityList, String examinationTypeName) {
        List<ClinicSearchListDTO> retVal = new ArrayList<>();
        ExaminationType et = this.getExaminationType(examinationTypeName);
        for (Clinic c : entityList) {
            retVal.add(this.clinicMapper.toDtoSearchList(c, et));
        }

        return retVal;
    }

    private boolean doesDoctorHaveTime(Doctor doctor, String examinationStartTime, String examinationEndTime, String date) {
        if (isThereTime(examinationStartTime) && isThereTime(examinationEndTime) && isThereDate(date)) {
            LocalTime examStart = LocalTime.parse(examinationStartTime);
            LocalTime examEnd = LocalTime.parse(examinationEndTime);
            LocalDate examDate = LocalDate.parse(date);

            List<Examination> examinations = this.examinationRepository.findAllActiveExaminationsOfDoctor(doctor.getId());
            for (Examination e : examinations) {
                LocalTime eStartTime = e.getStartDateTime().toLocalTime();
                LocalTime eEndTime = e.getEndDateTime().toLocalTime();
                if (!examDate.isEqual(e.getStartDateTime().toLocalDate()))
                    continue;
                if ((examStart.isBefore(eStartTime) && examEnd.isBefore(eStartTime)) || (examStart.isAfter(eEndTime) && examEnd.isAfter(eEndTime))) {
                    continue;
                } else {
                    return false;
                }
            }

            List<Operation> operations = this.operationRepository.findAllActiveOperationsOfDoctor(doctor.getId());
            for (Operation o : operations) {
                LocalTime oStartTime = o.getStartDateTime().toLocalTime();
                LocalTime oEndTime = o.getEndDateTime().toLocalTime();
                if (!examDate.isEqual(o.getStartDateTime().toLocalDate()))
                    continue;
                if ((examStart.isBefore(oStartTime) && examEnd.isBefore(oStartTime)) || (examStart.isAfter(oEndTime) && examEnd.isAfter(oEndTime))) {
                    continue;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isTimeInDoctorsShift(Doctor doctor, String examinationStartTime, String examinationEndTime) {

        if (!isThereTime(examinationStartTime) && !isThereTime(examinationEndTime)) {
            return true;
        }

        if (!isThereTime(examinationStartTime) && isThereTime(examinationEndTime)) {
            if (LocalTime.parse(examinationEndTime).isBefore(doctor.getShiftStart()) || LocalTime.parse(examinationEndTime).isAfter(doctor.getShiftEnd())) {
                return false;
            }
        }

        if (isThereTime(examinationStartTime) && !isThereTime(examinationEndTime)) {
            if (LocalTime.parse(examinationStartTime).isBefore(doctor.getShiftStart()) || LocalTime.parse(examinationStartTime).isAfter(doctor.getShiftEnd())) {
                return false;
            }
        }

        if (isThereTime(examinationStartTime) && isThereTime(examinationEndTime)) {
            LocalTime startTime = LocalTime.parse(examinationStartTime);
            LocalTime endTime = LocalTime.parse(examinationEndTime);
            if (doctor.getShiftEnd().isBefore(doctor.getShiftStart())) { //crossing midnight
                if (endTime.isBefore(startTime)) {
                    return !startTime.isBefore(doctor.getShiftStart()) && !endTime.isAfter(doctor.getShiftEnd());
                } else {
                    return !startTime.isAfter(doctor.getShiftEnd()) || !endTime.isBefore(doctor.getShiftStart());
                }

            } else {
                if (endTime.isBefore(startTime)) {
                    return !startTime.isAfter(doctor.getShiftEnd()) || !endTime.isBefore(doctor.getShiftStart());
                } else {
                    return !startTime.isBefore(doctor.getShiftStart()) && !endTime.isAfter(doctor.getShiftEnd());
                }

            }
        }

        return true;
    }

    private boolean doctorHasAbsence(Doctor doctor, String examinationDate) {
        List<DoctorAbsence> absences = this.doctorAbsenceRepository.findByStaff(doctor.getId());
        for (DoctorAbsence da : absences) {
            if (LocalDate.parse(examinationDate).isBefore(da.getStartDate()) || LocalDate.parse(examinationDate).isAfter(da.getEndDate())) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean containsName(Clinic clinic, String searchName) {
        return clinic.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private String dtoSearchValidation(SearchClinicDTO dto) {
        if (isThereName(dto.clinicName) && dto.clinicName.length() > 30) {
            return "Clinic's name cannot be longer than 30 characters.";
        }

        if (isThereType(dto.examinationType) && !containsType(dto.examinationType)) {
            return "Examination's type must be in the system.";
        }

        return "OK";
    }

    private boolean isThereTime(String searchTime) {
        return searchTime != null && !searchTime.equals("");
    }

    private boolean isThereDate(String searchDate) {
        return searchDate != null && !searchDate.equals("");
    }

    private boolean isThereName(String searchName) {
        return searchName != null && !searchName.equals("");
    }

    private boolean isThereType(String searchType) {
        return searchType != null && !searchType.equals("");
    }

    private boolean containsType(String type) {
        List<ExaminationType> types = this.examinationTypeRepository.findAll();
        for (ExaminationType et : types) {
            if (et.getName().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public List<DoctorSearchListDTO> searchDoctorsPatient(SearchDoctorPatientDTO searchDoctor) throws Exception {
        String validation = this.dtoValidPatientSearch(searchDoctor);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(searchDoctor.examinationDate, searchDoctor.examinationStart,
                searchDoctor.examinationEnd, searchDoctor.clinicName, searchDoctor.clinicId, searchDoctor.examinationType);
        List<Doctor> doctorsList = this.getValidDostors(searchDoctor.clinicId, searchClinicDTO);

        Iterator i = doctorsList.iterator();
        while (i.hasNext()) {
            Doctor doctor = (Doctor) i.next();
            if (isThereName(searchDoctor.searchName)) {
                if (!this.containsNameDoctor(doctor, searchDoctor.searchName)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereSurname(searchDoctor.searchSurname)) {
                if (!this.containsSurnameDoctor(doctor, searchDoctor.searchSurname)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereRating(searchDoctor.searchRating)) {
                if (doctor.getRatingAvg() != Double.parseDouble(searchDoctor.searchRating)) {
                    i.remove();
                    continue;
                }
            }
        }

        return this.toDoctorSearchForList(doctorsList, searchDoctor.examinationDate, searchDoctor.examinationStart, searchDoctor.examinationEnd);
    }

    public String dtoValidPatientSearch(SearchDoctorPatientDTO dto) {
        if (isThereNameDoctor(dto.searchName) && dto.searchName.length() > 30) {
            return "Doctor's name cannot be longer than 30 characters.";
        }

        if (isThereSurname(dto.searchSurname) && dto.searchSurname.length() > 30) {
            return "Doctor's surname cannot be longer than 30 characters.";
        }

        if (isThereRating(dto.searchRating)) { //rating
            try {
                double rating = Double.parseDouble(dto.searchRating);
                if (rating < 1 || rating > 10) {
                    return "Rating is number from 1 to 10.";
                }
            } catch (Exception e) {
                return "Rating is number from 1 to 10.";
            }
        }

        return "OK";
    }

    private boolean isThereRating(String rating) {
        return rating != null && !rating.equals("");
    }

    private boolean isThereNameDoctor(String searchName) {
        return searchName != null && !searchName.equals("");
    }

    private boolean isThereSurname(String searchSurname) {
        return searchSurname != null && !searchSurname.equals("");
    }

    private boolean containsNameDoctor(Doctor doctor, String searchName) {
        return doctor.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private boolean containsSurnameDoctor(Doctor doctor, String searchSurname) {
        return doctor.getSurname().toLowerCase().contains(searchSurname.toLowerCase());
    }

    private List<DoctorSearchListDTO> toDoctorSearchForList(List<Doctor> entity, String examinationDate, String examinationStart, String examinationEnd) {
        List<DoctorSearchListDTO> retVal = new ArrayList<>();
        for (Doctor doctor : entity) {
            retVal.add(this.doctorMapper.toDoctorSearchListDto(doctor, examinationDate, examinationStart, examinationEnd));
        }

        return retVal;
    }

}
