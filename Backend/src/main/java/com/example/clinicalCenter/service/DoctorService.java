package com.example.clinicalCenter.service;

import com.example.clinicalCenter.converter.ObjectsForRequest;
import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.ValidationException;
import com.example.clinicalCenter.mapper.BusinessDoctorDTO;
import com.example.clinicalCenter.mapper.DoctorMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.UserType;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService implements ServiceInterface<DoctorDTO> {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ExaminationTypeService examinationTypeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DoctorAbsenceService doctorAbsenceService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;

    private DoctorMapper doctorMapper;

    public DoctorService() {
        this.doctorMapper = new DoctorMapper();
    }

    @Override
    public List<DoctorDTO> findAll() {
        return null;
    }

    public List<User> findAllEntity() {
        return userRepository.findByType("DOCTOR");
    }

    @Override
    public DoctorDTO findOne(Long id) {
        return this.doctorMapper.toDto(this.doctorRepository.findById(id).get());
    }

    public Doctor findOneEntity(Long id) {
        return this.doctorRepository.findById(id).get();
    }

    public DoctorEditDTO findOneForEdit(Long id) {
        Doctor doctor = (Doctor) this.userRepository.findById(id).orElseGet(null);
        return this.doctorMapper.toDoctorEdit(doctor);
    }

    @Override
    public DoctorDTO create(DoctorDTO dto) throws Exception {
        String validationString = authenticationService.validateDoctor(dto);
        if (!validationString.equals("OK")) {
            throw new ValidationException(validationString);
        }
        Doctor doctor = this.doctorMapper.toEntity(dto);
        doctor.setPassword(userDetailsService.encodePassword(dto.password));
        List<Authority> authorities = new ArrayList<Authority>();
        Authority a = new Authority();
        a.setType(UserType.ROLE_DOCTOR);
        authorities.add(a);
        doctor.setAuthorities(authorities);
        doctor.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        doctor.setSpecialty(this.examinationTypeService.findOneEntity(dto.specialty));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            doctor.setClinic(clinicAdmin.getClinic());
        }

        this.emailService.sendToCreatedUser(doctor.getEmail(), dto.password);
        return this.doctorMapper.toDto(this.userRepository.save(doctor));
    }

    @Override
    public DoctorDTO update(DoctorDTO dto) throws Exception {
        return null;
    }


    public DoctorEditDTO update(DoctorEditDTO dto) throws Exception {
        String validation = editValidation(dto);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);
        Doctor updatedDoctor = this.doctorRepository.findById(dto.id).orElseGet(null);
        updatedDoctor.setName(dto.name);
        updatedDoctor.setSurname(dto.surname);
        updatedDoctor.setPhone(dto.phone);
        Doctor doctor = this.doctorRepository.save(updatedDoctor);
        return this.doctorMapper.toDoctorEdit(doctor);
    }

    public DoctorDTO findByEmail(String email) {
        Doctor entity = this.doctorRepository.findByEmail(email);
        return doctorMapper.toDto(entity);
    }

    public ArrayList<DoctorForListDTO> getDoctorsOfClinic() {
        ArrayList<DoctorForListDTO> doctorsOfClinic = new ArrayList<DoctorForListDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        for (User d : this.userRepository.findDoctorsOfClinic(clinicId)) {
            Doctor doctor = (Doctor) d;
            doctorsOfClinic.add(this.doctorMapper.toDoctorForList(doctor));
        }

        return doctorsOfClinic;
    }

    public ArrayList<DoctorSearchListDTO> getDoctorsOfClinic(Long clinicId) {
        ArrayList<DoctorSearchListDTO> doctorsOfClinic = new ArrayList<DoctorSearchListDTO>();
        for (User d : this.userRepository.findDoctorsOfClinic(clinicId)) {
            Doctor doctor = (Doctor) d;
            doctorsOfClinic.add(this.doctorMapper.toDoctorSearchListDto(doctor, "-", "-", "-"));
        }

        return doctorsOfClinic;
    }

    public List<Doctor> getDoctorsOfExaminationType(Long type) {
        return this.userRepository.findBySpecialty(type);
    }

    public boolean doesDoctorHaveOperationAtGivenTime(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Operation> doctorOperations = operationRepository.findAllActiveOperationsOfDoctor(doctor.getId());

        for (Operation operation : doctorOperations) {
            if (date.compareTo(operation.getStartDateTime().toLocalDate()) == 0) {
                if (startTime.isAfter(operation.getStartDateTime().toLocalTime()) && startTime.isBefore(operation.getEndDateTime().toLocalTime())) {
                    return true;
                } else if (endTime.isBefore(operation.getEndDateTime().toLocalTime()) && endTime.isAfter(operation.getStartDateTime().toLocalTime())) {
                    return true;
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }

        return false;
    }

    public ArrayList<DoctorForListDTO> getFreeDoctors(ExaminationParamsDTO examinationParams) {
        ArrayList<DoctorForListDTO> freeDoctors = new ArrayList<>();
        LocalDate examDate = LocalDate.parse(examinationParams.examDate);
        LocalTime startTime = LocalTime.parse(examinationParams.startTime);
        LocalTime endTime = LocalTime.parse(examinationParams.endTime);
        List<Doctor> list = this.userRepository.findBySpecialty(examinationParams.type);
        HashSet<Doctor> examinationDoctors = this.examinationService.getDoctorsOnExaminations(
                examinationParams.type, examDate, startTime, endTime);

        list.removeAll(examinationDoctors);

        List<Long> stuffOnAbsence = this.doctorAbsenceService.stuffOnAbsence(examDate);

        for (Doctor d : list) {
            if (d.getShiftEnd().isBefore(d.getShiftStart())) { //crossing midnight
                if (endTime.isBefore(startTime)) {
                    if (startTime.isBefore(d.getShiftStart()) || endTime.isAfter(d.getShiftEnd())) {
                        continue;
                    }
                } else {
                    if (startTime.isAfter(d.getShiftEnd()) && endTime.isBefore(d.getShiftStart())) {
                        continue;
                    }
                }

            } else {
                if (endTime.isBefore(startTime)) {
                    if (startTime.isAfter(d.getShiftEnd()) && endTime.isBefore(d.getShiftStart())) {
                        continue;
                    }
                } else {
                    if (startTime.isBefore(d.getShiftStart()) || endTime.isAfter(d.getShiftEnd())) {
                        continue;
                    }
                }

            }


            if (stuffOnAbsence.contains(d.getId())) {
                continue;
            }

            freeDoctors.add(this.doctorMapper.toDoctorForList(d));
        }

        return freeDoctors;
    }

    @Override
    public DoctorDTO delete(Long id) throws Exception {
        User user = this.userRepository.findById(id).orElseGet(null);
        if (user != null && user instanceof Doctor) {
            if (!this.examinationService.getAllScheduledAndInProgressExaminationsOfDoctor(id).isEmpty()) {
                throw new ValidationException("Selected doctor has some scheduled or in progress examinations.");
            }
            if (!user.isDeleted()) {
                user.delete();
            } else {
                throw new ValidationException("Doctor already deleted!");
            }
        }

        return this.doctorMapper.toDto((Doctor) this.userRepository.save(user));
    }

    public List<BusinessDoctorDTO> getDoctorsRates() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }

        return this.toBusinessDoctorDTOList(this.userRepository.findDoctorsOfClinic(clinicId));
    }

    private List<BusinessDoctorDTO> toBusinessDoctorDTOList(List<Doctor> entityList) {
        List<BusinessDoctorDTO> retVal = new ArrayList<>();
        for (Doctor doctor : entityList) {
            retVal.add(this.doctorMapper.toBusinessDoctorDTO(doctor));
        }

        return retVal;
    }

    private List<DoctorForListDTO> toDoctorForListDTOList(List<Doctor> entityList) {
        List<DoctorForListDTO> retVal = new ArrayList<>();
        for (Doctor doctor : entityList) {
            retVal.add(this.doctorMapper.toDoctorForList(doctor));
        }

        return retVal;
    }

    public List<DoctorForListDTO> searchDoctorsCA(SearchDoctorClinicAdminDTO searchDoctor) throws Exception {
        String validation = dtoSearchValid(searchDoctor);
        if (!validation.equals("OK"))
            throw new ValidationException(validation);

        List<Doctor> doctorsList = this.doctorRepository.findAll();

        Iterator i = doctorsList.iterator();
        while (i.hasNext()) {
            Doctor doctor = (Doctor) i.next();
            if (isThereName(searchDoctor.searchName)) {
                if (!this.containsName(doctor, searchDoctor.searchName)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereSurname(searchDoctor.searchSurname)) {
                if (!this.containsSurname(doctor, searchDoctor.searchSurname)) {
                    i.remove();
                    continue;
                }
            }

            if (isTherePhone(searchDoctor.searchPhone)) {
                if (!this.containsPhone(doctor, searchDoctor.searchPhone)) {
                    i.remove();
                    continue;
                }
            }

            if (isThereSpecialty(searchDoctor.searchSpecialty)) {
                if (doctor.getSpecialty().getId() != searchDoctor.searchSpecialty) {
                    i.remove();
                    continue;
                }
            }

            if (isThereShiftStart(searchDoctor.searchShiftStart)) {
                LocalTime shiftStart = LocalTime.parse(searchDoctor.searchShiftStart);
                if (shiftStart.isBefore(doctor.getShiftStart())) {
                    i.remove();
                    continue;
                }
            }

            if (isThereShiftEnd(searchDoctor.searchShiftEnd)) {
                LocalTime shiftEnd = LocalTime.parse(searchDoctor.searchShiftEnd);
                if (shiftEnd.isAfter(doctor.getShiftEnd())) {
                    i.remove();
                    continue;
                }
            }

            if (isThereShiftStart(searchDoctor.searchShiftStart) && isThereShiftEnd(searchDoctor.searchShiftEnd)) {
                LocalTime shiftStart = LocalTime.parse(searchDoctor.searchShiftStart);
                LocalTime shiftEnd = LocalTime.parse(searchDoctor.searchShiftEnd);
                if (shiftStart.isBefore(doctor.getShiftStart()) && shiftEnd.isAfter(doctor.getShiftEnd())) {
                    i.remove();
                    continue;
                }
            }

        }

        return this.toDoctorForListDTOList(doctorsList);
    }

    private boolean containsName(Doctor doctor, String searchName) {
        return doctor.getName().toLowerCase().contains(searchName.toLowerCase());
    }

    private boolean containsSurname(Doctor doctor, String searchSurname) {
        return doctor.getSurname().toLowerCase().contains(searchSurname.toLowerCase());
    }

    private boolean containsPhone(Doctor doctor, String phone) {
        return doctor.getPhone().toLowerCase().contains(phone.toLowerCase());
    }

    private String dtoSearchValid(SearchDoctorClinicAdminDTO dto) {
        if (isThereName(dto.searchName) && dto.searchName.length() > 30) {
            return "Doctor's name cannot be longer than 30 characters.";
        }

        if (isThereSurname(dto.searchSurname) && dto.searchSurname.length() > 30) {
            return "Doctor's surname cannot be longer than 30 characters.";
        }

        if (isTherePhone(dto.searchPhone)) {
            if (!(dto.searchPhone.matches("^[0-9]+$"))) {
                return "Phone number can only contain numeric values.";
            }
            if (dto.searchPhone.length() > 10) {
                return "Phone number must be max 10 characters length.";
            }
        }

        if (isThereShiftStart(dto.searchShiftStart)) {
            try {
                LocalTime.parse(dto.searchShiftStart);
            } catch (Exception e) {
                return "Shift start time input is not correct.";
            }
        }

        if (isThereShiftEnd(dto.searchShiftEnd)) {
            try {
                LocalTime.parse(dto.searchShiftEnd);
            } catch (Exception e) {
                return "Shift end time input is not correct.";
            }
        }

        return "OK";
    }

    private boolean isThereName(String searchName) {
        return searchName != null && !searchName.equals("");
    }

    private boolean isThereSurname(String searchSurname) {
        return searchSurname != null && !searchSurname.equals("");
    }

    private boolean isTherePhone(String searchPhone) {
        return searchPhone != null && !searchPhone.equals("");
    }

    private boolean isThereSpecialty(Long searchSpecialty) {
        return searchSpecialty != null;
    }

    private boolean isThereShiftStart(String searchShiftStart) {
        return searchShiftStart != null && !searchShiftStart.equals("");
    }

    private boolean isThereShiftEnd(String isThereShiftEnd) {
        return isThereShiftEnd != null && !isThereShiftEnd.equals("");
    }

    private String editValidation(DoctorEditDTO dto) {
        if (dto.name == null) {
            return "Name cannot be empty.";
        }
        if (!(dto.name.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Name cannot contain special characters.";
        }
        if (dto.name.length() > 30) {
            return "Name must not be longer than 30 characters.";
        }

        if (dto.surname == null) {
            return "Surname cannot be empty.";
        }
        if (!(dto.surname.matches("^[A-ZŠĐŽČĆ][a-zšđćčžA-ZŠĐŽČĆ ]*$"))) {
            return "Surname cannot contain special characters.";
        }
        if (dto.surname.length() > 30) {
            return "Surname must not be longer than 30 characters.";
        }

        if (dto.phone == null) {
            return "Phone cannot be empty.";
        }
        if (!(dto.phone.matches("^[0-9]+$"))) {
            return "Phone number can only contain numeric values.";
        }
        if (dto.phone.length() < 6 || dto.phone.length() > 10) {
            return "Phone number must be between 6 and 10 characters.";
        }

        return "OK";
    }

    public String isDoctorFreeAtGivenTime(LocalDate date, LocalTime startTime, LocalTime endTime, Doctor doctor) {

        List<Long> stuffOnAbsence = this.doctorAbsenceService.stuffOnAbsence(date);
        if (stuffOnAbsence.contains(doctor.getId())) {
            return "You are on absence at selected date.";
        }

        if (doctor.getShiftEnd().isBefore(doctor.getShiftStart())) { //crossing midnight
            if (endTime.isBefore(startTime)) {
                if (startTime.isBefore(doctor.getShiftStart()) || endTime.isAfter(doctor.getShiftEnd())) {
                    return "Your shift does not match given time";
                }
            } else {
                if (startTime.isAfter(doctor.getShiftEnd()) && endTime.isBefore(doctor.getShiftStart())) {
                    return "Your shift does not match given time";
                }
            }

        } else {
            if (endTime.isBefore(startTime)) {
                if (startTime.isAfter(doctor.getShiftEnd()) && endTime.isBefore(doctor.getShiftStart())) {
                    return "Your shift does not match given time";
                }
            } else {
                if (startTime.isBefore(doctor.getShiftStart()) || endTime.isAfter(doctor.getShiftEnd())) {
                    return "Your shift does not match given time";
                }
            }
        }

        HashSet<Doctor> examinationDoctors = this.examinationService.getDoctorsOnExaminations(
                doctor.getSpecialty().getId(), date, startTime, endTime);

        for (Doctor d : examinationDoctors) {
            if (d.getId() == doctor.getId()) {
                return "You already have examination at given time";
            }
        }

        if (doesDoctorHaveOperationAtGivenTime(doctor, date, startTime, endTime))
            return "You already have operation at given time";

        return "OK";
    }

    public List<DoctorForListDTO> getFreeDoctorsForRequest(RoomTimeDTO roomTime, boolean isAutomatic) {
        Long clinicId = (long) 0;
        if (!isAutomatic) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = this.userRepository.findByEmail(username);
            if (user instanceof ClinicAdmin) {
                ClinicAdmin clinicAdmin = (ClinicAdmin) user;
                clinicId = clinicAdmin.getClinic().getId();
            }
        } else {
            clinicId = this.roomRepository.findById(roomTime.id).get().getClinic().getId();
        }

        LocalDate date = LocalDate.parse(roomTime.date);
        LocalTime startTime = LocalTime.parse(roomTime.startTime);
        LocalTime endTime = LocalTime.parse(roomTime.endTime);

        List<Doctor> clinicDoctors = this.userRepository.findDoctorsOfClinic(clinicId);

        List<Doctor> freeDoctors = clinicDoctors.stream().filter(d -> {
            return this.isDoctorFreeAtGivenTime(date, startTime, endTime, d).equals("OK");
        }).collect(Collectors.toCollection(() -> new ArrayList<Doctor>()));

        return this.toDoctorForListDTOList(freeDoctors);

    }

    public ObjectsForRequest checkForDoctorInRequestList(List<RoomTimeDTO> list, ExaminationType type, boolean isExamination) {
        Random random = new Random();
        boolean isThereDoctor = false;
        ObjectsForRequest retVal = null;
        for (RoomTimeDTO roomTimeDTO : list) {
            List<DoctorForListDTO> freeDoctors = this.getFreeDoctorsForRequest(roomTimeDTO, true);

            if (!freeDoctors.isEmpty()) {
                int doctorIndex = random.nextInt(freeDoctors.size());
                Doctor doctor = this.doctorRepository.findById((freeDoctors.get(doctorIndex)).id).orElseGet(null);
                isThereDoctor = true;
                retVal = new ObjectsForRequest(roomTimeDTO, doctor);
            }
        }

        if (isThereDoctor) {
            return retVal;
        } else {
            int randomRoomTime = random.nextInt(list.size());
            RoomTimeDTO chosenRoomTime = list.get(randomRoomTime);
            String[] names = {"Milan", "Luka", "Stefan", "Djordje", "Lazar", "Danijel", "Nikola", "Nemanja", "Dragan", "Miroslav"};
            String[] surnames = {"Mikic", "Vunic", "Milosevic", "Bajic", "Prastalo", "Kobajagic", "Kesic", "Rogic", "Savic", "Jankovic"};
            int nameIndex = random.nextInt(names.length);
            int surnameIndex = random.nextInt(surnames.length);
            int phone = random.nextInt(9000000) + 1000000;
            Doctor newDoctor = new Doctor(getRandomEmail(), "qweqweqwe", names[nameIndex], surnames[surnameIndex], Integer.toString(phone),
                    (long) 0, 0.0, LocalTime.parse(chosenRoomTime.startTime).minusHours(3), LocalTime.parse(chosenRoomTime.endTime).plusHours(3));
            if (isExamination)
                newDoctor.setSpecialty(type);
            else {
                newDoctor.setSpecialty(examinationTypeService.findAllEntity().get(random.nextInt(examinationTypeService.findAllEntity().size())));
            }
            newDoctor.setClinic(type.getClinic());

            this.doctorRepository.save(newDoctor);
            return new ObjectsForRequest(chosenRoomTime, newDoctor);
        }
    }

    private String getRandomEmail() {
        String charArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * charArray.length());
            salt.append(charArray.charAt(index));
        }
        salt.append("@maildrop.cc");
        return salt.toString();
    }

}
