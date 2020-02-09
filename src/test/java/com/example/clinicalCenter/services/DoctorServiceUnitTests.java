package com.example.clinicalCenter.services;

import com.example.clinicalCenter.model.Clinic;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.ExaminationType;
import com.example.clinicalCenter.repository.OperationRepository;
import com.example.clinicalCenter.service.DoctorAbsenceService;
import com.example.clinicalCenter.service.DoctorService;
import com.example.clinicalCenter.service.ExaminationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DoctorServiceUnitTests {

    @Autowired
    private DoctorService doctorService;

    @MockBean
    private ExaminationService examinationServiceMocked;

    @MockBean
    private OperationRepository operationRepositoryMocked;

    @MockBean
    private DoctorAbsenceService doctorAbsenceServiceMocked;

    @Test
    public void testIsDoctorFreeAtGivenTimeOK() {
        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        Clinic clinic = new Clinic("Blagoja Parovica","Novi Sad","Fina klinika", "Gradska bolnica", 5L, 4.8);
        clinic.setId(2L);
        doctor.setClinic(clinic);
        ExaminationType type = new ExaminationType("Tip1", 50);
        type.setId(10L);
        doctor.setSpecialty(type);
        LocalDate date = LocalDate.of(2020,4,1);
        LocalTime startTime = LocalTime.of(12,0);
        LocalTime endTime = LocalTime.of(13,0);
        Mockito.when(doctorAbsenceServiceMocked.stuffOnAbsence(date)).thenReturn(new ArrayList<>());
        Mockito.when(examinationServiceMocked.getDoctorsOnExaminations(any(Long.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(new HashSet<>());
        Mockito.when(operationRepositoryMocked.findAllActiveOperationsOfDoctor(any(Long.class))).thenReturn(new ArrayList<>());
        String retVal = doctorService.isDoctorFreeAtGivenTime(date, startTime, endTime, doctor);

        Assert.assertNotNull(retVal);
        Assert.assertEquals("OK", retVal);
        verify(doctorAbsenceServiceMocked, times(1)).stuffOnAbsence(date);
        verify(examinationServiceMocked, times(1)).getDoctorsOnExaminations(any(Long.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        verify(operationRepositoryMocked, times(1)).findAllActiveOperationsOfDoctor(any(Long.class));
    }

    @Test
    public void testIsDoctorFreeAtGivenTimeNotOK() {
        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        Clinic clinic = new Clinic("Blagoja Parovica","Novi Sad","Fina klinika", "Gradska bolnica", 5L, 4.8);
        clinic.setId(2L);
        doctor.setClinic(clinic);
        ExaminationType type = new ExaminationType("Tip1", 50);
        type.setId(10L);
        doctor.setSpecialty(type);
        LocalDate date = LocalDate.of(2020,4,1);
        LocalTime startTime = LocalTime.of(12,0);
        LocalTime endTime = LocalTime.of(13,0);

        List<Long> absences = new ArrayList<>();
        absences.add(6L);
        Mockito.when(doctorAbsenceServiceMocked.stuffOnAbsence(date)).thenReturn(absences);
        Mockito.when(examinationServiceMocked.getDoctorsOnExaminations(any(Long.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(new HashSet<>());
        Mockito.when(operationRepositoryMocked.findAllActiveOperationsOfDoctor(any(Long.class))).thenReturn(new ArrayList<>());
        String retVal = doctorService.isDoctorFreeAtGivenTime(date, startTime, endTime, doctor);

        Assert.assertNotNull(retVal);
        Assert.assertNotEquals("OK", retVal);
        verify(doctorAbsenceServiceMocked, times(1)).stuffOnAbsence(date);
        verify(examinationServiceMocked, times(0)).getDoctorsOnExaminations(any(Long.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        verify(operationRepositoryMocked, times(0)).findAllActiveOperationsOfDoctor(any(Long.class));
    }
}
