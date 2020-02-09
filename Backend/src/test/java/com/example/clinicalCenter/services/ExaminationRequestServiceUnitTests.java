package com.example.clinicalCenter.services;

import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.ExaminationRequestDTO;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.model.enums.ExaminationRequestStatus;
import com.example.clinicalCenter.repository.ClinicRepository;
import com.example.clinicalCenter.repository.ExaminationRequestRepository;
import com.example.clinicalCenter.repository.ExaminationTypeRepository;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.service.DoctorService;
import com.example.clinicalCenter.service.ExaminationRequestService;
import com.example.clinicalCenter.service.ExaminationService;
import org.junit.Assert;
import org.junit.Before;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static com.example.clinicalCenter.constants.ClinicConstants.CLINIC_ID;
import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.RoomConstants.ROOM_ID;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ExaminationRequestServiceUnitTests {

    @Autowired
    private ExaminationRequestService examinationRequestService;

    @MockBean
    private ExaminationRequestRepository examinationRequestRepositoryMocked;

    @MockBean
    private UserRepository userRepositoryMocked;

    @MockBean
    private DoctorService doctorServiceMocked;

    @MockBean
    private ClinicRepository clinicRepositoryMocked;

    @MockBean
    private ExaminationTypeRepository examinationTypeRepositoryMocked;

    @MockBean
    private ExaminationService examinationServiceMocked;

    private Doctor doctor;

    private Clinic clinic;

    @Before
    public void onSetUp() {
        doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        clinic = new Clinic("Blagoja Parovica","Novi Sad","Fina klinika", "Gradska bolnica", 5L, 4.8);
        clinic.setId(2L);
        doctor.setClinic(clinic);
        ExaminationType type = new ExaminationType("Tip1", 50);
        type.setId(10L);
        doctor.setSpecialty(type);
        Authentication auth = new UsernamePasswordAuthenticationToken(doctor,null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testCreateExaminationRequest() throws Exception {
        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);
        Patient patient = new Patient(10L, "patient1@maildrop.cc", "123456789", "Sima", "Simic", "Stari Most",
                "Sremska Mitrovica", "Srbija", "12345670", "12345", null);
        ExaminationRequest examinationRequest = new ExaminationRequest(LocalDate.of(2020, 4, 1),
                LocalTime.of(12, 0), LocalTime.of(13, 0), doctor.getSpecialty(), null,
                doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);
        examinationRequest.setId(100L);
        examinationRequest.setClinic(clinic);
        Mockito.when(userRepositoryMocked.findByEmail(DOCTOR_EMAIL)).thenReturn(doctor);
        Mockito.when(userRepositoryMocked.findById(dto.patientId)).thenReturn(java.util.Optional.of(patient));
        Mockito.when(userRepositoryMocked.findById(any(Long.class))).thenReturn(java.util.Optional.of(patient));
        Mockito.when(doctorServiceMocked.isDoctorFreeAtGivenTime(any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), any(Doctor.class))).thenReturn("OK");
        Mockito.when(examinationRequestRepositoryMocked.save(any(ExaminationRequest.class))).thenReturn(examinationRequest);

        ExaminationRequestDTO retVal = examinationRequestService.createExaminationRequest(dto);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(examinationRequest.getId(), retVal.id);

        verify(userRepositoryMocked, times(1)).findByEmail(DOCTOR_EMAIL);
        verify(userRepositoryMocked, times(1)).findById(dto.patientId);
        verify(userRepositoryMocked, times(1)).findById(any(Long.class));
        verify(doctorServiceMocked, times(1)).isDoctorFreeAtGivenTime(any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), any(Doctor.class));
        verify(examinationRequestRepositoryMocked, times(1)).save(any(ExaminationRequest.class));
    }

    @Test
    public void testCreateExaminationRequestThrowsException() throws Exception {
        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);
        Patient patient = new Patient(10L, "patient1@maildrop.cc", "123456789", "Sima", "Simic", "Stari Most",
                "Sremska Mitrovica", "Srbija", "12345670", "12345", null);
        ExaminationRequest examinationRequest = new ExaminationRequest(LocalDate.of(2020, 4, 1),
                LocalTime.of(12, 0), LocalTime.of(13, 0), doctor.getSpecialty(), null,
                doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);
        examinationRequest.setId(100L);
        examinationRequest.setClinic(clinic);
        Mockito.when(userRepositoryMocked.findByEmail(DOCTOR_EMAIL)).thenReturn(doctor);
        Mockito.when(userRepositoryMocked.findById(any(Long.class))).thenReturn(java.util.Optional.of(patient));
        Mockito.when(doctorServiceMocked.isDoctorFreeAtGivenTime(any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), any(Doctor.class))).thenReturn("Error message");
        Mockito.when(examinationRequestRepositoryMocked.save(any(ExaminationRequest.class))).thenReturn(examinationRequest);

        ExaminationRequestDTO retVal = null;
        try {
            retVal = examinationRequestService.createExaminationRequest(dto);
            Assert.fail();
        }catch (Exception e) {
            Assert.assertNull(retVal);
        }

        verify(userRepositoryMocked, times(1)).findByEmail(DOCTOR_EMAIL);
        verify(userRepositoryMocked, times(0)).findById(any(Long.class));
        verify(doctorServiceMocked, times(1)).isDoctorFreeAtGivenTime(any(LocalDate.class), any(LocalTime.class), any(LocalTime.class), any(Doctor.class));
        verify(examinationRequestRepositoryMocked, times(0)).save(any(ExaminationRequest.class));
    }

    @Test
    public void testCreateExaminationRequestPatient() throws Exception {
        ExaminationRequestDTO dto = new ExaminationRequestDTO(EXAMINATION_REQUEST_ID, EXAM_DATE, START_TIME, END_TIME,
                EXAMINATION_TYPE_ID, DOCTOR_ID, CLINIC_ID, ROOM_ID, PATIENT_ID);

        Patient patient = new Patient(10L, "patient1@maildrop.cc", "123456789", "Sima",
                "Simic", "Stari Most",
                "Sremska Mitrovica", "Srbija", "12345670", "12345", null);

        ExaminationRequest examinationRequest = new ExaminationRequest(LocalDate.of(2020, 4, 1),
                LocalTime.of(12, 0), LocalTime.of(13, 0), doctor.getSpecialty(), null,
                doctor, patient, ExaminationRequestStatus.WAITING_FOR_ADMIN);
        examinationRequest.setId(100L);
        examinationRequest.setClinic(clinic);
        ExaminationType type = new ExaminationType("Tip1", 50);

        Mockito.when(examinationRequestRepositoryMocked.findById(any(Long.class))).thenReturn(Optional.of(examinationRequest));
        Mockito.when(clinicRepositoryMocked.findById(any(Long.class))).thenReturn(Optional.of(clinic));
        Mockito.when(userRepositoryMocked.findById(any(Long.class))).thenReturn(java.util.Optional.of(doctor));
        Mockito.when(userRepositoryMocked.findDoctorExaminationDetails(any(Long.class))).thenReturn(doctor);
        Mockito.when(examinationTypeRepositoryMocked.findById(any(Long.class))).thenReturn(Optional.of(type));
        Mockito.when(examinationServiceMocked.isDoctorFreeAtGivenTime(any(LocalDate.class),
                any(LocalTime.class), any(LocalTime.class), any(Doctor.class))).thenReturn("OK");
        Mockito.when(userRepositoryMocked.findClinicAdmins(any(Long.class))).thenReturn(new ArrayList<>());
        Mockito.when(examinationRequestRepositoryMocked.save(any(ExaminationRequest.class))).thenReturn(examinationRequest);

        ExaminationRequestDTO retVal = examinationRequestService.createExaminationRequestPatient(dto);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(examinationRequest.getId(), retVal.id);

        verify(examinationRequestRepositoryMocked, times(0)).findById(any(Long.class));
        verify(clinicRepositoryMocked, times(1)).findById(any(Long.class));
        verify(userRepositoryMocked, times(1)).findById(any(Long.class));
        verify(userRepositoryMocked, times(1)).findDoctorExaminationDetails(any(Long.class));
        verify(examinationServiceMocked, times(1)).isDoctorFreeAtGivenTime(any(LocalDate.class),
                any(LocalTime.class), any(LocalTime.class), any(Doctor.class));
        verify(userRepositoryMocked, times(1)).findClinicAdmins(any(Long.class));
        verify(examinationRequestRepositoryMocked, times(1)).save(any(ExaminationRequest.class));
        verify(examinationTypeRepositoryMocked, times(1)).findById(any(Long.class));
    }
}
