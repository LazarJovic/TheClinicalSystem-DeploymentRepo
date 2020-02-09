package com.example.clinicalCenter.services;

import com.example.clinicalCenter.dto.ExaminationDTO;
import com.example.clinicalCenter.dto.PredefinedExaminationDTO;
import com.example.clinicalCenter.exception.DateFormatException;
import com.example.clinicalCenter.exception.DoubleFormatException;
import com.example.clinicalCenter.exception.TimeFormatException;
import com.example.clinicalCenter.exception.WrongStatusException;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.service.ExaminationService;
import com.example.clinicalCenter.service.NurseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.RoomConstants.ROOM_ID;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ExaminationServiceUnitTests {

    @Autowired
    private ExaminationService examinationService;

    @MockBean
    private ExaminationRepository examinationRepositoryMocked;

    @MockBean
    private UserRepository userRepositoryMocked;

    @MockBean
    private ExaminationTypeRepository examinationTypeRepositoryMocked;

    @MockBean
    private ExaminationRequestRepository examinationRequestRepositoryMocked;

    @MockBean
    private NurseService nurseServiceMocked;

    @MockBean
    private RoomRepository roomRepositoryMocked;

    private User patient;

    public void onSetUpLoginPatient() {
        patient = new Patient(4L, "patient1@maildrop.cc", "123456789", "Sima", "Simic", "Stari Most",
                "Sremska Mitrovica", "Srbija", "12345670", "12345", null);

        Authentication auth = new UsernamePasswordAuthenticationToken(patient,null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void onSetUpLoginClinicAdmin() {
        ClinicAdmin clinicAdmin = new ClinicAdmin("clinicAdmin1@maildrop.cc", "123456789", "Pera", "Peric", "12345670", null);
        clinicAdmin.setId(3L);
        Authentication auth = new UsernamePasswordAuthenticationToken(patient,null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testScheduleExaminationReturnsValue() throws Exception {

        this.onSetUpLoginPatient();

        PredefinedExaminationDTO dto = new PredefinedExaminationDTO(EXAMINATION_ID, TYPE_NAME, EXAM_DATE, START_TIME, END_TIME,
                PRICE_WITH_DISCOUNT, DOCTOR_NAME, NURSE_NAME, ROOM_NAME);

        LocalDateTime start = LocalDateTime.of(LocalDate.parse(EXAM_DATE), LocalTime.parse(START_TIME));
        LocalDateTime end = LocalDateTime.of(LocalDate.parse(EXAM_DATE), LocalTime.parse(END_TIME));

        User user = new Patient(PATIENT_ID, PATIENT_EMAIL, PASSWORD, NAME, SURNAME, ADDRESS,
                CITY, COUNTRY, PHONE, SOCIAL_SECURITY_NUMBER, null);

        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 0L, 0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);

        Nurse nurse = new Nurse(NURSE_EMAIL, PASSWORD, NAME, SURNAME, PHONE);

        ExaminationType examinationType = new ExaminationType(EXAMINATION_TYPE_NAME, EXAMINATION_TYPE_PRICE);

        Room room = new Room(ROOM_ID, ROOM_NAME, null);

        Examination examination = new Examination(start, end, examinationType, room, doctor, nurse,
                null, STATUS_AVAILABLE, 0);
        examination.setId(EXAMINATION_ID);

        Mockito.when(examinationRepositoryMocked.findById(EXAMINATION_ID)).thenReturn(Optional.of(examination));
        Mockito.when(userRepositoryMocked.findByEmail(PATIENT_EMAIL)).thenReturn(user);
        Mockito.when(examinationRepositoryMocked.save(examination)).thenReturn(examination);
        Mockito.when(examinationTypeRepositoryMocked.findById(EXAMINATION_ID)).thenReturn(Optional.of(examinationType));

        PredefinedExaminationDTO retVal = this.examinationService.schedule(dto);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(EXAMINATION_ID, retVal.id);

        verify(examinationRepositoryMocked, times(1)).findById(EXAMINATION_ID);
        verify(userRepositoryMocked, times(1)).findByEmail(PATIENT_EMAIL);
        verify(examinationRepositoryMocked, times(1)).save(examination);

    }

    @Test
    public void testScheduleExaminationThrowsException() {

        this.onSetUpLoginPatient();

        PredefinedExaminationDTO dto = new PredefinedExaminationDTO(EXAMINATION_ID, TYPE_NAME, EXAM_DATE, START_TIME, END_TIME,
                PRICE_WITH_DISCOUNT, DOCTOR_NAME, NURSE_NAME, ROOM_NAME);

        LocalDateTime start = LocalDateTime.of(LocalDate.parse(EXAM_DATE), LocalTime.parse(START_TIME));
        LocalDateTime end = LocalDateTime.of(LocalDate.parse(EXAM_DATE), LocalTime.parse(END_TIME));

        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 0L, 0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);

        Nurse nurse = new Nurse(NURSE_EMAIL, PASSWORD, NAME, SURNAME, PHONE);

        ExaminationType examinationType = new ExaminationType(EXAMINATION_TYPE_NAME, EXAMINATION_TYPE_PRICE);

        Room room = new Room(ROOM_ID, ROOM_NAME, null);

        Examination examination = new Examination(start, end, examinationType, room, doctor, nurse,
                null, STATUS_SCHEDULED, 0);
        examination.setId(EXAMINATION_ID);

        User user = new Patient(PATIENT_ID, PATIENT_EMAIL, PASSWORD, NAME, SURNAME, ADDRESS,
                CITY, COUNTRY, PHONE, SOCIAL_SECURITY_NUMBER, null);

        Mockito.when(examinationRepositoryMocked.findById(EXAMINATION_ID)).thenReturn(Optional.of(examination));
        Mockito.when(userRepositoryMocked.findByEmail(PATIENT_EMAIL)).thenReturn(user);
        Mockito.when(examinationRepositoryMocked.save(examination)).thenReturn(examination);

        assertThrows(Exception.class, () -> this.examinationService.schedule(dto));

        verify(examinationRepositoryMocked, times(1)).findById(EXAMINATION_ID);
        verify(userRepositoryMocked, times(1)).findByEmail(PATIENT_EMAIL);
        verify(examinationRepositoryMocked, times(0)).save(examination);
    }

    @Test
    public void testCreateExaminationFromRequestReturnsValue() throws Exception {
        this.onSetUpLoginClinicAdmin();

        ExaminationDTO dto = new ExaminationDTO(0L, 0L, EXAM_DATE, START_TIME, END_TIME, "", DOCTOR_ID,
                14L, ROOM_ID, PATIENT_ID);

        LocalTime startTime = LocalTime.parse(START_TIME);
        LocalTime endTime = LocalTime.parse(END_TIME);
        LocalDate examDate = LocalDate.parse(EXAM_DATE);
        LocalDateTime start = LocalDateTime.of(examDate, startTime);
        LocalDateTime end = LocalDateTime.of(examDate, endTime);

        Clinic clinic = new Clinic("Gradska bolnica", "Futoska", "Novi Sad", "Fina klinika", 20L, 4.6);
        clinic.setId(2L);

        Patient patient = new Patient(PATIENT_ID, PATIENT_EMAIL, PASSWORD, NAME, SURNAME, ADDRESS,
                CITY, COUNTRY, PHONE, SOCIAL_SECURITY_NUMBER, null);

        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 0L, 0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        doctor.setClinic(clinic);

        Nurse nurse = new Nurse(NURSE_EMAIL, PASSWORD, NAME, SURNAME, PHONE);
        nurse.setId(NURSE_ID);
        nurse.setClinic(clinic);

        ExaminationType examinationType = new ExaminationType(EXAMINATION_TYPE_NAME, EXAMINATION_TYPE_PRICE);
        examinationType.setId(10L);
        examinationType.setClinic(clinic);

        Room room = new Room(ROOM_ID, ROOM_NAME, RoomType.ORDINATION);
        room.setClinic(clinic);

        ExaminationRequest examinationRequest = new ExaminationRequest(examDate, startTime, endTime, examinationType,
                null, doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);
        examinationRequest.setId(14L);

        Examination examination = new Examination(start, end, examinationType, room, doctor, nurse,
                patient, STATUS_SCHEDULED, 0);
        examination.setId(NEW_EXAMINATION_ID);

        Mockito.when(userRepositoryMocked.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        Mockito.when(userRepositoryMocked.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        Mockito.when(examinationRepositoryMocked.save(any(Examination.class))).thenReturn(examination);
        Mockito.when(roomRepositoryMocked.findChosenRoom(ROOM_ID)).thenReturn(room);
        Mockito.when(nurseServiceMocked.getRandomFreeNurse(examDate, startTime, endTime, clinic)).thenReturn(nurse);
        Mockito.when(examinationRequestRepositoryMocked.save(examinationRequest)).thenReturn(examinationRequest);
        Mockito.when(examinationRequestRepositoryMocked.findById(14L)).thenReturn(Optional.of(examinationRequest));

        ExaminationDTO retVal = this.examinationService.create(dto);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(NEW_EXAMINATION_ID, retVal.id);

        verify(userRepositoryMocked, times(1)).findById(PATIENT_ID);
        verify(examinationRepositoryMocked, times(1)).save(any(Examination.class));
        verify(userRepositoryMocked, times(1)).findById(DOCTOR_ID);
        verify(roomRepositoryMocked, times(1)).findChosenRoom(ROOM_ID);
        verify(nurseServiceMocked, times(1)).getRandomFreeNurse(examDate, startTime, endTime, clinic);
        verify(examinationRequestRepositoryMocked, times(1)).save(examinationRequest);
        verify(examinationRequestRepositoryMocked, times(1)).findById(14L);

    }

    @Test
    public void testCreateExaminationFromRequestThrowsExceptionWrongDateInput() throws Exception {
        this.onSetUpLoginClinicAdmin();

        ExaminationDTO dto = new ExaminationDTO(0L, 0L, BAD_DATE_FORMAT, START_TIME, END_TIME, "", DOCTOR_ID,
                14L, ROOM_ID, PATIENT_ID);

        LocalTime startTime = LocalTime.parse(START_TIME);
        LocalTime endTime = LocalTime.parse(END_TIME);
        LocalDate examDate = LocalDate.parse(EXAM_DATE);
        LocalDateTime start = LocalDateTime.of(examDate, startTime);
        LocalDateTime end = LocalDateTime.of(examDate, endTime);

        Clinic clinic = new Clinic("Gradska bolnica", "Futoska", "Novi Sad", "Fina klinika", 20L, 4.6);
        clinic.setId(2L);

        Patient patient = new Patient(PATIENT_ID, PATIENT_EMAIL, PASSWORD, NAME, SURNAME, ADDRESS,
                CITY, COUNTRY, PHONE, SOCIAL_SECURITY_NUMBER, null);

        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 0L, 0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        doctor.setClinic(clinic);

        Nurse nurse = new Nurse(NURSE_EMAIL, PASSWORD, NAME, SURNAME, PHONE);
        nurse.setId(NURSE_ID);
        nurse.setClinic(clinic);

        ExaminationType examinationType = new ExaminationType(EXAMINATION_TYPE_NAME, EXAMINATION_TYPE_PRICE);
        examinationType.setId(10L);
        examinationType.setClinic(clinic);

        Room room = new Room(ROOM_ID, ROOM_NAME, RoomType.ORDINATION);
        room.setClinic(clinic);

        ExaminationRequest examinationRequest = new ExaminationRequest(examDate, startTime, endTime, examinationType,
                null, doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);
        examinationRequest.setId(14L);

        Examination examination = new Examination(start, end, examinationType, room, doctor, nurse,
                patient, STATUS_SCHEDULED, 0);
        examination.setId(NEW_EXAMINATION_ID);

        Mockito.when(userRepositoryMocked.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        Mockito.when(userRepositoryMocked.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        Mockito.when(examinationRepositoryMocked.save(any(Examination.class))).thenReturn(examination);
        Mockito.when(roomRepositoryMocked.findChosenRoom(ROOM_ID)).thenReturn(room);
        Mockito.when(nurseServiceMocked.getRandomFreeNurse(examDate, startTime, endTime, clinic)).thenReturn(nurse);
        Mockito.when(examinationRequestRepositoryMocked.save(examinationRequest)).thenReturn(examinationRequest);
        Mockito.when(examinationRequestRepositoryMocked.findById(14L)).thenReturn(Optional.of(examinationRequest));

        assertThrows(DateFormatException.class, () -> this.examinationService.create(dto));

        verify(userRepositoryMocked, times(0)).findById(PATIENT_ID);
        verify(examinationRepositoryMocked, times(0)).save(any(Examination.class));
        verify(userRepositoryMocked, times(0)).findById(DOCTOR_ID);
        verify(roomRepositoryMocked, times(0)).findChosenRoom(ROOM_ID);
        verify(nurseServiceMocked, times(0)).getRandomFreeNurse(examDate, startTime, endTime, clinic);
        verify(examinationRequestRepositoryMocked, times(0)).save(examinationRequest);
        verify(examinationRequestRepositoryMocked, times(0)).findById(14L);

    }

    @Test
    public void testCreateExaminationFromRequestThrowsExceptionWrongTimeInput() throws Exception {
        this.onSetUpLoginClinicAdmin();

        ExaminationDTO dto = new ExaminationDTO(0L, 0L, EXAM_DATE, BAD_TIME_FORMAT, END_TIME, "", DOCTOR_ID,
                14L, ROOM_ID, PATIENT_ID);

        assertThrows(TimeFormatException.class, () -> this.examinationService.create(dto));

    }

    @Test
    public void testCreateExaminationFromRequestThrowsExceptionWrongDiscountInput() throws Exception {
        this.onSetUpLoginClinicAdmin();

        ExaminationDTO dto = new ExaminationDTO(0L, 0L, EXAM_DATE, BAD_TIME_FORMAT, END_TIME, BAD_DISCOUNT_FORMAT, DOCTOR_ID,
                14L, ROOM_ID, PATIENT_ID);

        assertThrows(DoubleFormatException.class, () -> this.examinationService.create(dto));

    }

    @Test
    public void testCreateExaminationFromRequestThrowsExceptionWrongRequestStatus() throws Exception {
        this.onSetUpLoginClinicAdmin();

        ExaminationDTO dto = new ExaminationDTO(0L, 0L, EXAM_DATE, START_TIME, END_TIME, "", DOCTOR_ID,
                14L, ROOM_ID, PATIENT_ID);

        LocalTime startTime = LocalTime.parse(START_TIME);
        LocalTime endTime = LocalTime.parse(END_TIME);
        LocalDate examDate = LocalDate.parse(EXAM_DATE);
        LocalDateTime start = LocalDateTime.of(examDate, startTime);
        LocalDateTime end = LocalDateTime.of(examDate, endTime);

        Clinic clinic = new Clinic("Gradska bolnica", "Futoska", "Novi Sad", "Fina klinika", 20L, 4.6);
        clinic.setId(2L);

        Patient patient = new Patient(PATIENT_ID, PATIENT_EMAIL, PASSWORD, NAME, SURNAME, ADDRESS,
                CITY, COUNTRY, PHONE, SOCIAL_SECURITY_NUMBER, null);

        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 0L, 0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        doctor.setClinic(clinic);

        Nurse nurse = new Nurse(NURSE_EMAIL, PASSWORD, NAME, SURNAME, PHONE);
        nurse.setId(NURSE_ID);
        nurse.setClinic(clinic);

        ExaminationType examinationType = new ExaminationType(EXAMINATION_TYPE_NAME, EXAMINATION_TYPE_PRICE);
        examinationType.setId(10L);
        examinationType.setClinic(clinic);

        Room room = new Room(ROOM_ID, ROOM_NAME, RoomType.ORDINATION);
        room.setClinic(clinic);

        ExaminationRequest examinationRequest = new ExaminationRequest(examDate, startTime, endTime, examinationType,
                null, doctor, patient, ExaminationRequestStatus.CONFIRMED);
        examinationRequest.setId(14L);

        Examination examination = new Examination(start, end, examinationType, room, doctor, nurse,
                patient, STATUS_SCHEDULED, 0);
        examination.setId(NEW_EXAMINATION_ID);

        Mockito.when(userRepositoryMocked.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        Mockito.when(userRepositoryMocked.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        Mockito.when(examinationRepositoryMocked.save(any(Examination.class))).thenReturn(examination);
        Mockito.when(roomRepositoryMocked.findChosenRoom(ROOM_ID)).thenReturn(room);
        Mockito.when(nurseServiceMocked.getRandomFreeNurse(examDate, startTime, endTime, clinic)).thenReturn(nurse);
        Mockito.when(examinationRequestRepositoryMocked.save(examinationRequest)).thenReturn(examinationRequest);
        Mockito.when(examinationRequestRepositoryMocked.findById(14L)).thenReturn(Optional.of(examinationRequest));

        assertThrows(WrongStatusException.class, () -> this.examinationService.create(dto));

        verify(userRepositoryMocked, times(1)).findById(PATIENT_ID);
        verify(examinationRepositoryMocked, times(0)).save(any(Examination.class));
        verify(userRepositoryMocked, times(1)).findById(DOCTOR_ID);
        verify(roomRepositoryMocked, times(1)).findChosenRoom(ROOM_ID);
        verify(nurseServiceMocked, times(1)).getRandomFreeNurse(examDate, startTime, endTime, clinic);
        verify(examinationRequestRepositoryMocked, times(0)).save(examinationRequest);
        verify(examinationRequestRepositoryMocked, times(1)).findById(14L);

    }

}
