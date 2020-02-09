package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.*;
import com.example.clinicalCenter.exception.*;
import com.example.clinicalCenter.mapper.ExaminationMapper;
import com.example.clinicalCenter.mapper.PredefinedExaminationMapper;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@ComponentScan
@Service
@Transactional(readOnly = true)
public class ExaminationService implements ServiceInterface<ExaminationDTO> {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ExaminationRequestRepository examinationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ExaminationTypeRepository examinationTypeRepository;

    @Autowired
    private ClinicRatingRepository clinicRatingRepository;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private EmailService emailService;

    private ExaminationMapper examinationMapper;

    private PredefinedExaminationMapper predefinedExaminationMapper;

    @Autowired
    private DoctorRatingRepository doctorRatingRepository;

    public ExaminationService() {
        this.examinationMapper = new ExaminationMapper();
        this.predefinedExaminationMapper = new PredefinedExaminationMapper();
    }

    @Override
    public List<ExaminationDTO> findAll() {
        return null;
    }

    @Override
    public ExaminationDTO findOne(Long id) {
        return examinationMapper.toDto(examinationRepository.findById(id).get());
    }


    public Examination findOneEntity(Long id) {
        return this.examinationRepository.findById(id).get();
    }

    public ExaminationCalendarDetailedDTO findOneForCalendar(Long id) {
        return examinationMapper.toCalendarDetailedDTO(examinationRepository.findById(id).get());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ExaminationDTO create(ExaminationDTO dto) throws Exception {
        String validateTime = validateTime(dto.startTime, dto.endTime);
        String validateDate = validateDate(dto.examDate);
        if (!dto.discount.equals("")) {
            String validateDiscount = validateDiscount(dto.discount);
            if (!validateDiscount.equals("OK")) {
                throw new DoubleFormatException(validateDiscount);
            }
        }

        if (!validateTime.equals("OK")) {
            throw new TimeFormatException(validateTime);
        } else if (!validateDate.equals("OK")) {
            throw new DateFormatException(validateDate);
        }

        Examination examination = new Examination();

        LocalDate date = LocalDate.parse(dto.examDate);
        LocalTime startTime = LocalTime.parse(dto.startTime);
        LocalTime endTime = LocalTime.parse(dto.endTime);

        examination.setStartDateTime(LocalDateTime.of(date, startTime));
        examination.setEndDateTime(LocalDateTime.of(date, endTime));
        Doctor doctor = (Doctor) this.userRepository.findById(dto.doctor).orElseGet(null);
        examination.setDoctor(doctor);
        examination.setClinic(doctor.getClinic());
        Room chosenRoom = this.roomRepository.findChosenRoom(dto.room);
        examination.setRoom(chosenRoom);

        //for predefined examination
        if (dto.type != 0) {
            examination.setDiscount(Double.parseDouble(dto.discount));
            examination.setType(this.examinationTypeRepository.findById(dto.type).orElseGet(null));
            examination.setNurse((Nurse) this.userRepository.findById(dto.nurse).orElseGet(null));
            examination.setPredefined(true);
            examination.setStatus(Status.AVAILABLE);
        } else {
            examination.setDiscount(0);
            examination.setType(doctor.getSpecialty());
            examination.setPredefined(false);
            examination.setStatus(Status.WAITING_FOR_PATIENT);
            examination.setNurse(this.nurseService.getRandomFreeNurse(date, startTime, endTime, doctor.getClinic()));
            Patient patient = (Patient) this.userRepository.findById(dto.patient).orElseGet(null);
            examination.setPatient(patient);

            ExaminationRequest request = this.examinationRequestRepository.findById(dto.nurse).orElseGet(null);

            if (request.getStatus() != ExaminationRequestStatus.WAITING_FOR_ADMIN)
                throw new WrongStatusException("Wrong examination request status!");

            if (request != null) {
                request.setStatus(ExaminationRequestStatus.CONFIRMED);
                this.examinationRequestRepository.save(request);
            }

            this.emailService.sendToPatientForExamination(patient.getEmail(), dto.examDate, dto.startTime, dto.endTime);
        }

        Examination createdExamination = this.examinationRepository.save(examination);
        return this.examinationMapper.toDto(createdExamination);
    }

    @Override
    public ExaminationDTO update(ExaminationDTO dto) throws Exception {
        return null;
    }

    public ExaminationDTO updateEntity(Examination e) {
        return examinationMapper.toDto(examinationRepository.save(e));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public PredefinedExaminationDTO schedule(PredefinedExaminationDTO dto) throws Exception {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Patient p = null;
        if (user instanceof Patient) {
            p = (Patient) user;
        }
        Examination e = this.examinationRepository.findById(dto.id).get();
        e.setPatient(p);

        if (e.getStatus() == Status.SCHEDULED) {
            throw new GenericConflictException("This examination has already been scheduled.");
        }

        e.setStatus(Status.SCHEDULED);
        Examination u = this.examinationRepository.save(e);
        this.emailService.sendWhenSchedulingPredefinedExamination(user.getEmail());

        return this.predefinedExaminationMapper.toDto(u);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public PredefinedExaminationDTO cancel(PredefinedExaminationDTO dto) {
        Examination e = this.examinationRepository.findById(dto.id).get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examinationDateTime = e.getStartDateTime();

        long hours = now.until(examinationDateTime, ChronoUnit.HOURS);

        if (hours < 24) {
            return null;
        }

        if (e.isPredefined()) {
            e.setStatus(Status.AVAILABLE);
            e.setPatient(null);
        } else {
            e.setStatus(Status.CANCELED);
        }

        Examination u = this.examinationRepository.save(e);
        return this.predefinedExaminationMapper.toDto(u);
    }

    @Transactional(readOnly = false)
    public AppointmentDTO doctorCanceling(AppointmentDTO dto) {
        Examination e = this.examinationRepository.findById(dto.id).get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examinationDateTime = e.getStartDateTime();

        long hours = now.until(examinationDateTime, ChronoUnit.HOURS);

        if (hours < 24) {
            return null;
        }

        if (e.isPredefined()) {
            e.setStatus(Status.AVAILABLE);
            e.setPatient(null);
        } else {
            e.setStatus(Status.CANCELED);
        }

        Examination u = this.examinationRepository.save(e);
        return this.examinationMapper.toAppointmentCancelDTO(u);
    }

    @Transactional(readOnly = false)
    public AppointmentDTO startExamination(AppointmentDTO dto) throws Exception {
        return startExaminationFromId(dto.id);
    }

    @Transactional(readOnly = false)
    public AppointmentDTO startExaminationFromId(Long id) throws Exception {
        Examination e = this.examinationRepository.findById(id).get();

        if (!this.canExaminationStart(e.getId())) {
            throw new CannotStartException("You can start examination in the period of 15 minutes before or after examination start time.");
        }
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!examinationRepository.findInProgressExaminationsOfDoctor(user.getId()).isEmpty()) {
            throw new AlreadyInProgressException("Cannot start because you already have an examination in progress!");
        }
        if (!operationRepository.findInProgressOperationsOfDoctor(user.getId()).isEmpty()) {
            throw new AlreadyInProgressException("Cannot start because you already have an operation in progress!");
        }
        e.setStatus(Status.IN_PROGRESS);

        Examination examination = this.examinationRepository.save(e);
        return this.examinationMapper.toAppointmentDTO(examination);
    }

    public boolean canExaminationStart(Long examinationId) {
        Examination e = this.examinationRepository.findById(examinationId).get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime examinationDateTime = e.getStartDateTime();

        long minutes = 0;

        if (now.isBefore(examinationDateTime)) {
            minutes = now.until(examinationDateTime, ChronoUnit.MINUTES);
        } else {
            minutes = examinationDateTime.until(now, ChronoUnit.MINUTES);
        }

        return minutes <= 15 && e.getStatus() == Status.SCHEDULED;

    }

    @Override
    public ExaminationDTO delete(Long id) {
        return null;
    }

    public List<ExaminationCalendarDTO> getExaminationsOfLoggedIn() throws Exception {
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user instanceof Doctor) {
            List<Examination> examinations = examinationRepository.findExaminationsOfDoctor(user.getId());
            List<ExaminationCalendarDTO> retVal = new ArrayList<ExaminationCalendarDTO>();
            for (Examination e : examinations) {
                retVal.add(examinationMapper.toCalendarDTO(e));
            }
            return retVal;
        } else if (user instanceof Nurse) {
            List<Examination> examinations = examinationRepository.findExaminationsOfNurse(user.getId());
            List<ExaminationCalendarDTO> retVal = new ArrayList<ExaminationCalendarDTO>();
            for (Examination e : examinations) {
                retVal.add(examinationMapper.toCalendarDTO(e));
            }
            return retVal;
        } else {
            throw new BadRequestException();
        }
    }

    public List<Examination> getAllScheduledAndInProgressExaminationsOfDoctor(Long doctorId) {
        return this.examinationRepository.findScheduledAndInProgressExaminationsOfDoctor(doctorId);
    }

    public List<Examination> getAlAvailableAndScheduledAndInProgressExaminationsOfNurse(Long nurseId) {
        return this.examinationRepository.findAvailableAndScheduledAndInProgressExaminationsOfNurse(nurseId);
    }

    public List<Examination> getAllScheduledAndInProgressExaminationsOfRoom(Long roomId) {
        return this.examinationRepository.findScheduledAndInProgressExaminationsOfRoom(roomId);
    }

    public List<Examination> getAllScheduledAndInProgressExaminationsOfExaminationType(Long typeId) {
        return this.examinationRepository.findScheduledAndInProgressExaminationsOfType(typeId);
    }

    public List<Examination> getAllExaminationsForCreatingPredefinedExaminatons(Long type, LocalDate examDate, LocalTime startTime, LocalTime endTime,
                                                                                boolean doctors) {
        List<Examination> list = null;
        if (doctors) {
            list = this.examinationRepository.findByExaminationTypeAndStatus(type);
        } else {
            list = this.examinationRepository.findAll();
        }
        Iterator i = list.iterator();
        while (i.hasNext()) {
            Examination e = (Examination) i.next();
            if (e.getStartDateTime().toLocalDate().compareTo(examDate) != 0) {
                i.remove();
                continue;
            }
            if (!this.isThereConflictBetweenTheseTwoTimesForDoctor(startTime, endTime, e.getStartDateTime().toLocalTime(), e.getEndDateTime().toLocalTime())) {
                i.remove();
                continue;
            }

        }

        return list;
    }

    public List<Examination> getAllExaminationsForCreatingPredefinedExaminationsClinicAdmin(Long type, LocalDate examDate, LocalTime startTime, LocalTime endTime,
                                                                                boolean doctors) {
        List<Examination> list = null;
        if (doctors) {
            list = this.examinationRepository.findByExaminationTypeAndStatus(type);
        } else {
            list = this.examinationRepository.findAll();
        }
        Iterator i = list.iterator();
        while (i.hasNext()) {
            Examination e = (Examination) i.next();
            if (e.getStartDateTime().toLocalDate().compareTo(examDate) != 0) {
                i.remove();
                continue;
            }
            if (!this.isThereConflictBetweenTheseTwoTimes(startTime, endTime, e.getStartDateTime().toLocalTime(), e.getEndDateTime().toLocalTime())) {
                i.remove();
                continue;
            }

        }

        return list;
    }

    public boolean isNurseAvailableForAbsence(Long userId, /*String nurse_email, */LocalDate startDate, LocalDate endDate) {
        for (Examination e : this.examinationRepository.findAllActiveExaminationsOfNurse(userId)) {
            LocalDate start = e.getStartDateTime().toLocalDate();
            LocalDate end = e.getEndDateTime().toLocalDate();
            if ((startDate.isBefore(start) || startDate.isEqual(start))
                    && (endDate.isAfter(end) || endDate.isEqual(end))
                    && e.getStatus() != Status.FINISHED)
                return false;
        }
        return true;
    }

    public boolean isDoctorAvailableForAbsence(Long userId, LocalDate startDate, LocalDate endDate) {
        for (Examination e : this.examinationRepository.findAllActiveExaminationsOfDoctor(userId)) {
            LocalDate start = e.getStartDateTime().toLocalDate();
            LocalDate end = e.getEndDateTime().toLocalDate();
            if ((startDate.isBefore(start) || startDate.isEqual(start))
                    && (endDate.isAfter(end) || endDate.isEqual(end))) {
                return false;
            }
        }
        return true;
    }


    public HashSet<Doctor> getDoctorsOnExaminations(Long type, LocalDate examDate,
                                                    LocalTime startTime, LocalTime endTime) {
        List<Examination> examinationList = this.getAllExaminationsForCreatingPredefinedExaminationsClinicAdmin(type, examDate,
                startTime, endTime, true);
        List<Doctor> doctors = new ArrayList<>();
        for (Examination e : examinationList) {
            doctors.add(e.getDoctor());
        }

        return new HashSet<Doctor>(doctors);
    }

    public HashSet<Nurse> getNursesOnExaminations(Long type, LocalDate examDate,
                                                  LocalTime startTime, LocalTime endTime) {
        List<Examination> examinationList = this.getAllExaminationsForCreatingPredefinedExaminationsClinicAdmin(type, examDate,
                startTime, endTime, false);
        List<Nurse> nurses = new ArrayList<>();
        for (Examination e : examinationList) {
            nurses.add(e.getNurse());
        }

        return new HashSet<Nurse>(nurses);
    }

    public HashSet<Room> getRoomsOnExaminations(Long type, LocalDate examDate,
                                                LocalTime startTime, LocalTime endTime) {
        List<Examination> examinationList = this.getAllExaminationsForCreatingPredefinedExaminationsClinicAdmin(type, examDate,
                startTime, endTime, false);
        List<Room> rooms = new ArrayList<>();
        for (Examination e : examinationList) {
            rooms.add(e.getRoom());
        }

        return new HashSet<Room>(rooms);
    }

    public ArrayList<PredefinedExaminationDTO> getAllPredefinedExaminationsOfClinic(Long clinicId) {

        ArrayList<PredefinedExaminationDTO> examinations = new ArrayList<PredefinedExaminationDTO>();
        List<Examination> listOfPredefined = this.examinationRepository.findAvailablePredefinedExaminationsOfClinic(clinicId);
        for (Examination e : listOfPredefined) {
            //examinations.add(this.examinationMapper.toDto(e));
            PredefinedExaminationDTO dto = this.predefinedExaminationMapper.toDto(e);
            examinations.add(dto);
        }

        return examinations;
    }

    public ArrayList<PredefinedExaminationDTO> getAllScheduledExaminations() {

        ArrayList<PredefinedExaminationDTO> examinations = new ArrayList<PredefinedExaminationDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long userId = (long) 0;
        if (user instanceof Patient) {
            userId = user.getId();
        }
        List<Examination> listOfPredefined = this.examinationRepository.findAll();
        for (Examination e : listOfPredefined) {
            try {
                if (e.getStatus() == Status.SCHEDULED && userId == e.getPatient().getId()) {
                    PredefinedExaminationDTO dto = this.predefinedExaminationMapper.toDto(e);
                    examinations.add(dto);
                }
            } catch (Exception ex) {
                continue;
            }
        }

        return examinations;
    }

    public ArrayList<PredefinedExaminationDTO> getAllFinishedExaminations() {

        ArrayList<PredefinedExaminationDTO> examinations = new ArrayList<PredefinedExaminationDTO>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long userId = (long) 0;
        if (user instanceof Patient) {
            userId = user.getId();
        }

        for (Examination e : this.examinationRepository.findAllFinishedExaminationsOfPatient(userId)) {
            examinations.add(this.predefinedExaminationMapper.toDto(e));
        }

        return examinations;
    }

    public List<BusinessBarDTO> getDailyNumbers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Examination> finishedClinicExaminations = this.examinationRepository.findAllFinishedExaminationsOfClinic(clinicId);
        int[] intArray = new int[]{0, 0, 0, 0, 0, 0, 0};
        for (Examination e : finishedClinicExaminations) {
            if (e.getStartDateTime().getDayOfWeek() == DayOfWeek.MONDAY) {
                intArray[0]++;
            } else if (e.getStartDateTime().getDayOfWeek() == DayOfWeek.TUESDAY) {
                intArray[1]++;
            } else if (e.getStartDateTime().getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                intArray[2]++;
            } else if (e.getStartDateTime().getDayOfWeek() == DayOfWeek.THURSDAY) {
                intArray[3]++;
            } else if (e.getStartDateTime().getDayOfWeek() == DayOfWeek.FRIDAY) {
                intArray[4]++;
            } else if (e.getStartDateTime().getDayOfWeek() == DayOfWeek.SATURDAY) {
                intArray[5]++;
            } else {
                intArray[6]++;
            }
        }

        List<BusinessBarDTO> examinationBars = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            examinationBars.add(new BusinessBarDTO("Examination", intArray[i]));
        }

        return examinationBars;
    }


    public List<BusinessBarDTO> getWeeklyNumbers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Examination> finishedClinicExaminations = this.examinationRepository.findAllFinishedExaminationsOfClinic(clinicId);
        int[] intArray = new int[]{0, 0, 0, 0, 0};
        for (Examination e : finishedClinicExaminations) {
            if (e.getStartDateTime().getDayOfMonth() >= 1 && e.getStartDateTime().getDayOfMonth() <= 7) {
                intArray[0]++;
            } else if (e.getStartDateTime().getDayOfMonth() >= 8 && e.getStartDateTime().getDayOfMonth() <= 14) {
                intArray[1]++;
            } else if (e.getStartDateTime().getDayOfMonth() >= 15 && e.getStartDateTime().getDayOfMonth() <= 21) {
                intArray[2]++;
            } else if (e.getStartDateTime().getDayOfMonth() >= 22 && e.getStartDateTime().getDayOfMonth() <= 28) {
                intArray[3]++;
            } else if (e.getStartDateTime().getDayOfMonth() >= 29 && e.getStartDateTime().getDayOfMonth() <= 31) {
                intArray[4]++;
            }
        }

        List<BusinessBarDTO> examinationBars = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            examinationBars.add(new BusinessBarDTO("Examination", intArray[i]));
        }

        return examinationBars;
    }


    public List<BusinessBarDTO> getMonthlyNumbers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Examination> finishedClinicExaminations = this.examinationRepository.findAllFinishedExaminationsOfClinic(clinicId);
        int[] intArray = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (Examination e : finishedClinicExaminations) {
            if (e.getStartDateTime().getMonth() == Month.JANUARY) {
                intArray[0]++;
            } else if (e.getStartDateTime().getMonth() == Month.FEBRUARY) {
                intArray[1]++;
            } else if (e.getStartDateTime().getMonth() == Month.MARCH) {
                intArray[2]++;
            } else if (e.getStartDateTime().getMonth() == Month.APRIL) {
                intArray[3]++;
            } else if (e.getStartDateTime().getMonth() == Month.MAY) {
                intArray[4]++;
            } else if (e.getStartDateTime().getMonth() == Month.JUNE) {
                intArray[5]++;
            } else if (e.getStartDateTime().getMonth() == Month.JULY) {
                intArray[6]++;
            } else if (e.getStartDateTime().getMonth() == Month.AUGUST) {
                intArray[7]++;
            } else if (e.getStartDateTime().getMonth() == Month.SEPTEMBER) {
                intArray[8]++;
            } else if (e.getStartDateTime().getMonth() == Month.OCTOBER) {
                intArray[9]++;
            } else if (e.getStartDateTime().getMonth() == Month.NOVEMBER) {
                intArray[10]++;
            } else if (e.getStartDateTime().getMonth() == Month.DECEMBER) {
                intArray[11]++;
            }
        }

        List<BusinessBarDTO> examinationBars = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            examinationBars.add(new BusinessBarDTO("Examination", intArray[i]));
        }

        return examinationBars;
    }

    public double getIncomeFromExaminations(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        Long clinicId = (long) 0;
        if (user instanceof ClinicAdmin) {
            ClinicAdmin clinicAdmin = (ClinicAdmin) user;
            clinicId = clinicAdmin.getClinic().getId();
        }
        List<Examination> finishedClinicExaminations = this.examinationRepository.findAllFinishedExaminationsOfClinic(clinicId);
        double sum = 0;
        if (startDate != null && endDate == null) {
            for (Examination e : finishedClinicExaminations) {
                if (e.getStartDateTime().toLocalDate().isAfter(startDate)) {
                    sum += e.getType().getPrice();
                }
            }
        }
        if (startDate == null && endDate != null) {
            for (Examination e : finishedClinicExaminations) {
                if (e.getStartDateTime().toLocalDate().isBefore(endDate)) {
                    sum += e.getType().getPrice();
                }
            }
        }
        if (startDate != null && endDate != null) {
            for (Examination e : finishedClinicExaminations) {
                if (e.getStartDateTime().toLocalDate().isAfter(startDate) && e.getEndDateTime().toLocalDate().isBefore(endDate)) {
                    sum += e.getType().getPrice();
                }
            }
        }

        return sum;
    }

    private String validateDiscount(String discount) {
        try {
            Double.parseDouble(discount);
        } catch (Exception e) {
            return "Discount is not in correct format.";
        }
        return "OK";
    }

    private String validateTime(String startTime, String endTime) {
        try {
            LocalTime.parse(startTime);
            LocalTime.parse(endTime);
        } catch (Exception e) {
            return "Time input is not correct.";
        }
        return "OK";
    }

    private String validateDate(String examDate) {
        try {
            LocalDate.parse(examDate);
        } catch (Exception e) {
            return "Date input is not correct.";
        }
        return "OK";
    }

    public List<AppointmentDTO> getAllUpcomingExaminations() {
        List<AppointmentDTO> upcomingExaminations = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(username);
        for (Examination e : this.examinationRepository.getAllScheduledExaminationsOfDoctor(user.getId())) {
            long minutes = e.getStartDateTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            if (minutes > 15) {
                e.setStatus(Status.CANCELED);
                this.examinationRepository.save(e);
                continue;
            }
            upcomingExaminations.add(this.examinationMapper.toAppointmentDTO(e));
        }

        return upcomingExaminations;
    }

    public RatingDoctorAndClinicDTO getInfoForRating(Long id) {

        Examination e = this.findOneEntity(id);

        Patient p = e.getPatient();
        Doctor d = null;
        Clinic c = null;
        int clinicRating = 0;
        int doctorRating = 0;

        Object unproxiedEntityD = Hibernate.unproxy(e.getDoctor());
        try {
            d = (Doctor) unproxiedEntityD;

            Object unproxiedEntityC = Hibernate.unproxy(d.getClinic());
            c = (Clinic) unproxiedEntityC;
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }

        if (this.clinicRatingRepository.findByPatientAndClinic(c.getId(), p.getId()) != null) {
            ClinicRating cr = this.clinicRatingRepository.findByPatientAndClinic(c.getId(), p.getId());
            clinicRating = cr.getRating();
        }

        if (this.doctorRatingRepository.findByPatientAndDoctor(d.getId(), p.getId()) != null) {
            DoctorRating dr = this.doctorRatingRepository.findByPatientAndDoctor(d.getId(), p.getId());
            doctorRating = dr.getRating();
        }

        return new RatingDoctorAndClinicDTO(d.getId(), d.getName(), d.getSurname(), doctorRating, c.getId(), c.getName(), c.getCity(), c.getAddress(),
                clinicRating, p.getId());
    }

    public boolean isRoomAvailableForGivenTime(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Examination> examinationsOfRoom = this.examinationRepository.findAllActiveExaminationsOfRoom(room.getId());

        Iterator i = examinationsOfRoom.iterator();
        while (i.hasNext()) {
            Examination e = (Examination) i.next();
            if (e.getStartDateTime().toLocalDate().compareTo(date) != 0) {
                i.remove();
                continue;
            }
            if (!this.isThereConflictBetweenTheseTwoTimes(startTime, endTime, e.getStartDateTime().toLocalTime(), e.getEndDateTime().toLocalTime())) {
                i.remove();
                continue;
            }
        }

        return examinationsOfRoom.isEmpty();
    }

    public boolean isThereConflictBetweenTheseTwoTimes(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return (isAfterOrEqual(startTime1, startTime2) && isBeforeOrEqual(startTime1, endTime2)
                || isAfterOrEqual(endTime1, startTime2) && isBeforeOrEqual(endTime1, endTime2))
                || (startTime1.isBefore(startTime2) && endTime1.isAfter(endTime2));
    }

    public boolean isThereConflictBetweenTheseTwoTimesForDoctor(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        return (isAfter(startTime1, startTime2) && isBefore(startTime1, endTime2)
                || isAfter(endTime1, startTime2) && isBefore(endTime1, endTime2))
                || (startTime1.isBefore(startTime2) && endTime1.isAfter(endTime2));
    }

    public boolean isBeforeOrEqual(LocalTime time1, LocalTime time2) {
        return (time1.isBefore(time2) || time1.compareTo(time2) == 0);
    }

    public boolean isAfterOrEqual(LocalTime time1, LocalTime time2) {
        return (time1.isAfter(time2) || time1.compareTo(time2) == 0);
    }

    public boolean isBefore(LocalTime time1, LocalTime time2) {
        return (time1.isBefore(time2));
    }

    public boolean isAfter(LocalTime time1, LocalTime time2) {
        return (time1.isAfter(time2));
    }

    public List<Examination> getSortedExaminationsOfRoom(Long roomId, LocalDate date, LocalTime endTime) {
        LocalDateTime dateTime = LocalDateTime.of(date, endTime);
        List<Examination> examinationsAfter = new ArrayList<>();
        for (Examination e : this.examinationRepository.findAllActiveExaminationsOfRoomSorted(roomId)) {
            if (!dateTime.isAfter(e.getEndDateTime())) {
                examinationsAfter.add(e);
            }
        }

        return examinationsAfter;
    }

    public List<AppointmentDTO> getWaitingForPatientExaminations() {
        List<AppointmentDTO> waitingForPatient = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = (Patient) this.userRepository.findByEmail(username);
        List<Examination> list = this.examinationRepository.findAllWaitingForPatientOfPatient(patient.getId());

        list.forEach((examination) ->
                {
                    waitingForPatient.add(this.examinationMapper.toAppointmentDTO(examination));
                }
        );

        return waitingForPatient;
    }

    @Transactional(readOnly = false)
    public AppointmentDTO confirmExamination(Long id) {
        Examination examination = this.examinationRepository.findById(id).orElseGet(null);
        if (examination != null && examination.getStatus().equals(Status.WAITING_FOR_PATIENT)) {
            examination.setStatus(Status.SCHEDULED);
            emailService.sendSimpleMessage(examination.getDoctor().getEmail(),
                    "Examination scheduled",
                    "Examination on " + examination.getStartDateTime().toLocalDate().toString() + " at " +
                            examination.getStartDateTime().toLocalTime().toString() + " has been scheduled");

            this.examinationRepository.save(examination);
            return examinationMapper.toAppointmentDTO(examination);
        }
        return null;
    }

    @Transactional(readOnly = false)
    public AppointmentDTO denyExamination(Long id) {
        Examination examination = this.examinationRepository.findById(id).orElseGet(null);
        if (examination != null && examination.getStatus().equals(Status.WAITING_FOR_PATIENT)) {
            examination.setStatus(Status.CANCELED);
            this.examinationRepository.save(examination);
            return examinationMapper.toAppointmentDTO(examination);
        }
        return null;
    }

    public List<ExaminationDTO> getExaminationsInProgressOfLoggedIn() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Examination> examinations = examinationRepository.findInProgressExaminationsOfDoctor(user.getId());
        ArrayList<ExaminationDTO> retVal = new ArrayList<>();
        for (Examination e : examinations) {
            retVal.add(examinationMapper.toDto(e));
        }
        return retVal;
    }

    public String isDoctorFreeAtGivenTime(LocalDate date, LocalTime startTime, LocalTime endTime, Doctor doctor) {

        List<ExaminationRequest> requests = this.examinationRequestRepository.findAllRequestsForAdmin(doctor.getClinic().getId());
        for (ExaminationRequest er : requests) {
            if (er.getExaminationDate().isEqual(date)) {
                if (this.isThereConflictBetweenTheseTwoTimes(startTime, endTime, er.getStartTime(), er.getEndTime())) {
                    return "Not ok";
                }
            }
        }

        return "OK";
    }

}
