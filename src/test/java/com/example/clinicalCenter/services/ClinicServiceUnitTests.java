package com.example.clinicalCenter.services;

import com.example.clinicalCenter.dto.ClinicSearchListDTO;
import com.example.clinicalCenter.dto.DoctorSearchListDTO;
import com.example.clinicalCenter.dto.SearchClinicDTO;
import com.example.clinicalCenter.dto.SearchDoctorPatientDTO;
import com.example.clinicalCenter.model.*;
import com.example.clinicalCenter.repository.*;
import com.example.clinicalCenter.service.ClinicService;
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

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.clinicalCenter.constants.ClinicConstants.*;
import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ClinicServiceUnitTests {

    @Autowired
    private ClinicService clinicService;

    @MockBean
    private ClinicRepository clinicRepositoryMocked;

    @MockBean
    private UserRepository userRepositoryMocked;

    @MockBean
    private DoctorAbsenceRepository doctorAbsenceRepositoryMocked;

    @MockBean
    private ExaminationRepository examinationRepositoryMocked;

    @MockBean
    private OperationRepository operationRepositoryMocked;

    @MockBean
    private ExaminationTypeRepository examinationTypeRepositoryMocked;

    @Before
    public void onSetUp() {
        // add principal object to SecurityContextHolder
        User patient = new Patient(PATIENT_ID, PATIENT_EMAIL, PASSWORD, NAME, SURNAME, ADDRESS,
                CITY, COUNTRY, PHONE, SOCIAL_SECURITY_NUMBER, null);
        /* fill user object */
        Authentication auth = new UsernamePasswordAuthenticationToken(patient,null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testSearchClinicReturnsValueNoTypeChosen() throws Exception {

        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, "",
                null, null);

        Clinic c1 = new Clinic(CLINIC_NAME, ADDRESS, CITY, DESCRIPTION, RATING_COUNT, RATING_AVG);
        Clinic c2 = new Clinic(NEW_CLINIC_NAME, ADDRESS, CITY, DESCRIPTION, RATING_COUNT, RATING_AVG);
        c1.setId(CLINIC_ID);
        c2.setId(NEW_CLINIC_ID);
        List<Clinic> clinics = new ArrayList<>();
        clinics.add(c1);
        clinics.add(c2);

        Doctor doctor1 = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME2, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor2 = new Doctor(DOCTOR_EMAIL2, PASSWORD, NAME2, SURNAME2, PHONE, 5L, 4.5, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor3 = new Doctor(DOCTOR_EMAIL3, PASSWORD, NAME3, SURNAME3, PHONE, 2L, 4.0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor4 = new Doctor(DOCTOR_EMAIL4, PASSWORD, NAME4, SURNAME4, PHONE, 20L, 3.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor1.setId(DOCTOR1_ID);
        doctor2.setId(DOCTOR2_ID);
        doctor3.setId(DOCTOR3_ID);
        doctor4.setId(DOCTOR4_ID);
        doctor1.setClinic(c1);
        doctor2.setClinic(c1);
        doctor3.setClinic(c1);
        doctor4.setClinic(c2);

        List<Doctor> doctorsClinic1 = new ArrayList<>();
        doctorsClinic1.add(doctor1);
        doctorsClinic1.add(doctor2);
        doctorsClinic1.add(doctor3);

        List<Doctor> doctorsClinic2 = new ArrayList<>();
        doctorsClinic2.add(doctor4);

        Mockito.when(this.clinicRepositoryMocked.findAll()).thenReturn(clinics);
        // na obe klinike postoje lekari koji su slobodni u to vreme, tog datuma
        Mockito.when(this.userRepositoryMocked.findDoctorsOfClinic(c1.getId())).thenReturn(doctorsClinic1);
        Mockito.when(this.userRepositoryMocked.findDoctorsOfClinic(c2.getId())).thenReturn(doctorsClinic2);
        // nijedan doktor nema odsustvo
        for(Doctor d: doctorsClinic1)
            Mockito.when(this.doctorAbsenceRepositoryMocked.findByStaff(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic2)
            Mockito.when(this.doctorAbsenceRepositoryMocked.findByStaff(d.getId())).thenReturn(new ArrayList<>());
        // nijedan dokror nema drugu opraciju ni pregled
        for(Doctor d: doctorsClinic1)
            Mockito.when(this.examinationRepositoryMocked.findAllActiveExaminationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic2)
            Mockito.when(this.examinationRepositoryMocked.findAllActiveExaminationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic1)
            Mockito.when(this.operationRepositoryMocked.findAllActiveOperationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic2)
            Mockito.when(this.operationRepositoryMocked.findAllActiveOperationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());

        List<ClinicSearchListDTO> retVal = this.clinicService.searchClinic(searchClinicDTO);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(retVal.size(), clinics.size());
        Long[] actuals = {retVal.get(0).id, retVal.get(1).id};
        Long[] expecteds = {clinics.get(0).getId(), clinics.get(1).getId()};
        Assert.assertArrayEquals(expecteds, actuals);

        verify(this.examinationTypeRepositoryMocked, times(1)).findAll();
        verify(this.clinicRepositoryMocked, times(1)).findAll();
        verify(this.userRepositoryMocked, times(2)).findDoctorsOfClinic(any(Long.class));
        verify(this.doctorAbsenceRepositoryMocked, times(4)).findByStaff(any(Long.class));
        verify(this.examinationRepositoryMocked, times(4)).findAllActiveExaminationsOfDoctor(any(Long.class));
        verify(this.operationRepositoryMocked, times(4)).findAllActiveOperationsOfDoctor(any(Long.class));
    }

    @Test
    public void testSearchClinicReturnsValueWithTypeChosen() throws Exception {

        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, "",
                null, EXAMINATION_TYPE_NAME);

        ExaminationType et = new ExaminationType(EXAMINATION_TYPE_NAME, EXAMINATION_TYPE_PRICE);
        et.setId(EXAMINATION_TYPE_ID);
        List<ExaminationType> examTypes = new ArrayList<>();
        examTypes.add(et);

        Clinic c1 = new Clinic(CLINIC_NAME, ADDRESS, CITY, DESCRIPTION, RATING_COUNT, RATING_AVG);
        Clinic c2 = new Clinic(NEW_CLINIC_NAME, ADDRESS, CITY, DESCRIPTION, RATING_COUNT, RATING_AVG);
        c1.setId(CLINIC_ID);
        c2.setId(NEW_CLINIC_ID);
        List<Clinic> clinics = new ArrayList<>();
        clinics.add(c1);
        clinics.add(c2);

        Doctor doctor1 = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME2, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor2 = new Doctor(DOCTOR_EMAIL2, PASSWORD, NAME2, SURNAME2, PHONE, 5L, 4.5, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor3 = new Doctor(DOCTOR_EMAIL3, PASSWORD, NAME3, SURNAME3, PHONE, 2L, 4.0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor4 = new Doctor(DOCTOR_EMAIL4, PASSWORD, NAME4, SURNAME4, PHONE, 20L, 3.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor1.setId(DOCTOR1_ID);
        doctor2.setId(DOCTOR2_ID);
        doctor3.setId(DOCTOR3_ID);
        doctor4.setId(DOCTOR4_ID);
        doctor1.setClinic(c1);
        doctor2.setClinic(c1);
        doctor3.setClinic(c1);
        doctor4.setClinic(c2);
        doctor1.setSpecialty(et);
        doctor2.setSpecialty(et);
        doctor3.setSpecialty(et);
        doctor4.setSpecialty(et);

        List<Doctor> doctorsClinic1 = new ArrayList<>();
        doctorsClinic1.add(doctor1);
        doctorsClinic1.add(doctor2);
        doctorsClinic1.add(doctor3);

        List<Doctor> doctorsClinic2 = new ArrayList<>();
        doctorsClinic2.add(doctor4);

        // conatinsType
        Mockito.when(this.examinationTypeRepositoryMocked.findAll()).thenReturn(examTypes);
        Mockito.when(this.clinicRepositoryMocked.findAll()).thenReturn(clinics);
        // na obe klinike postoje lekari koji su slobodni u to vreme, tog datuma
        Mockito.when(this.userRepositoryMocked.findDoctorsOfClinicBySpecialty(c1.getId(), et.getId())).thenReturn(doctorsClinic1);
        Mockito.when(this.userRepositoryMocked.findDoctorsOfClinicBySpecialty(c2.getId(), et.getId())).thenReturn(doctorsClinic2);
        // nijedan doktor nema odsustvo
        for(Doctor d: doctorsClinic1)
            Mockito.when(this.doctorAbsenceRepositoryMocked.findByStaff(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic2)
            Mockito.when(this.doctorAbsenceRepositoryMocked.findByStaff(d.getId())).thenReturn(new ArrayList<>());
        // nijedan dokror nema drugu opraciju ni pregled
        for(Doctor d: doctorsClinic1)
            Mockito.when(this.examinationRepositoryMocked.findAllActiveExaminationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic2)
            Mockito.when(this.examinationRepositoryMocked.findAllActiveExaminationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic1)
            Mockito.when(this.operationRepositoryMocked.findAllActiveOperationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());
        for(Doctor d: doctorsClinic2)
            Mockito.when(this.operationRepositoryMocked.findAllActiveOperationsOfDoctor(d.getId())).thenReturn(new ArrayList<>());

        List<ClinicSearchListDTO> retVal = this.clinicService.searchClinic(searchClinicDTO);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(retVal.size(), clinics.size());
        Long[] actuals = {retVal.get(0).id, retVal.get(1).id};
        Long[] expecteds = {clinics.get(0).getId(), clinics.get(1).getId()};
        Assert.assertArrayEquals(expecteds, actuals);

        verify(this.examinationTypeRepositoryMocked, times(4)).findAll();
        verify(this.clinicRepositoryMocked, times(1)).findAll();
        verify(this.userRepositoryMocked, times(2)).findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class));
        verify(this.doctorAbsenceRepositoryMocked, times(4)).findByStaff(any(Long.class));
        verify(this.examinationRepositoryMocked, times(4)).findAllActiveExaminationsOfDoctor(any(Long.class));
        verify(this.operationRepositoryMocked, times(4)).findAllActiveOperationsOfDoctor(any(Long.class));
    }

    @Test
    public void testSearchClinicThrowsException_NoType() throws Exception {
        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, "",
                null, "Pregled");
        Mockito.when(this.examinationTypeRepositoryMocked.findAll()).thenReturn(new ArrayList<>());
        assertThrows(Exception.class, () -> this.clinicService.searchClinic(searchClinicDTO));
        verify(this.examinationTypeRepositoryMocked, times(1)).findAll();
    }

    @Test
    public void testSearchClinicThrowsExceptionLongName() throws Exception {
        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, LONG_CLINIC_NAME,
                null, "");

        assertThrows(Exception.class, () -> this.clinicService.searchClinic(searchClinicDTO));
    }

  @Test
  public void testSearchDoctorsPatient1() throws Exception {

        Doctor doctor1 = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME2, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor2 = new Doctor(DOCTOR_EMAIL2, PASSWORD, NAME2, SURNAME2, PHONE, 5L, 4.5, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor3 = new Doctor(DOCTOR_EMAIL3, PASSWORD, NAME3, SURNAME3, PHONE, 2L, 4.0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor4 = new Doctor(DOCTOR_EMAIL4, PASSWORD, NAME4, SURNAME4, PHONE, 20L, 3.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor1.setId(1L);
        doctor2.setId(2L);
        doctor3.setId(3L);
        doctor4.setId(4L);
        Clinic clinic = new Clinic();
        clinic.setId(8L);
        doctor1.setClinic(clinic);
        doctor2.setClinic(clinic);
        doctor3.setClinic(clinic);
        doctor4.setClinic(clinic);
        ExaminationType type1 = new ExaminationType("Pregled ociju", 100);
        ExaminationType type2 = new ExaminationType("Pregled usiju", 100);
        ExaminationType type3 = new ExaminationType("Pregled usta", 100);
        type1.setId(5L);
        type1.setId(6L);
        type1.setId(7L);
        doctor1.setSpecialty(type1);
        doctor2.setSpecialty(type1);
        doctor3.setSpecialty(type2);
        doctor4.setSpecialty(type3);

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctor1);
        doctors.add(doctor2);
        doctors.add(doctor3);
        doctors.add(doctor4);

        List<ExaminationType> types = new ArrayList<>();
        types.add(type1);
        types.add(type2);
        types.add(type3);

        List<Doctor> doctorsType1 = new ArrayList<>();
        doctorsType1.add(doctor1);
        doctorsType1.add(doctor2);

        Mockito.when(userRepositoryMocked.findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class))).thenReturn(doctorsType1);
        Mockito.when(userRepositoryMocked.findDoctorsOfClinic(any(Long.class))).thenReturn(doctors);
        Mockito.when(doctorAbsenceRepositoryMocked.findByStaff(any(Long.class))).thenReturn(new ArrayList<>());
        Mockito.when(examinationTypeRepositoryMocked.findAll()).thenReturn(types);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO(null, null, null,
                null, null, null, null, 8L, "Pregled ociju");

        List<DoctorSearchListDTO> retVal = this.clinicService.searchDoctorsPatient(dto);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(2, retVal.size());
        Long[] actuals = {retVal.get(0).id, retVal.get(1).id};
        Long[] expecteds = {doctors.get(0).getId(), doctors.get(1).getId()};
        Assert.assertArrayEquals(expecteds, actuals);

        verify(userRepositoryMocked, times(1)).findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class));
        verify(userRepositoryMocked, times(0)).findDoctorsOfClinic(any(Long.class));
        verify(doctorAbsenceRepositoryMocked, times(0)).findByStaff(any(Long.class));
        verify(examinationTypeRepositoryMocked, times(1)).findAll();
    }

    @Test
    public void testSearchDoctorsPatient2() throws Exception {

        Doctor doctor1 = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor2 = new Doctor(DOCTOR_EMAIL2, PASSWORD, NAME2, SURNAME2, PHONE, 5L, 4.5, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor3 = new Doctor(DOCTOR_EMAIL3, PASSWORD, NAME3, SURNAME3, PHONE, 2L, 4.0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor4 = new Doctor(DOCTOR_EMAIL4, PASSWORD, NAME4, SURNAME4, PHONE, 20L, 3.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor1.setId(1L);
        doctor2.setId(2L);
        doctor3.setId(3L);
        doctor4.setId(4L);
        Clinic clinic = new Clinic();
        clinic.setId(8L);
        doctor1.setClinic(clinic);
        doctor2.setClinic(clinic);
        doctor3.setClinic(clinic);
        doctor4.setClinic(clinic);
        ExaminationType type1 = new ExaminationType("Pregled ociju", 100);
        ExaminationType type2 = new ExaminationType("Pregled usiju", 100);
        ExaminationType type3 = new ExaminationType("Pregled usta", 100);
        type1.setId(5L);
        type1.setId(6L);
        type1.setId(7L);
        doctor1.setSpecialty(type1);
        doctor2.setSpecialty(type1);
        doctor3.setSpecialty(type2);
        doctor4.setSpecialty(type3);

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctor1);
        doctors.add(doctor2);
        doctors.add(doctor3);
        doctors.add(doctor4);

        List<ExaminationType> types = new ArrayList<>();
        types.add(type1);
        types.add(type2);
        types.add(type3);

        List<Doctor> doctorsType1 = new ArrayList<>();
        doctorsType1.add(doctor1);
        doctorsType1.add(doctor2);

        Mockito.when(userRepositoryMocked.findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class))).thenReturn(doctorsType1);
        Mockito.when(userRepositoryMocked.findDoctorsOfClinic(any(Long.class))).thenReturn(doctors);
        Mockito.when(doctorAbsenceRepositoryMocked.findByStaff(any(Long.class))).thenReturn(new ArrayList<>());
        Mockito.when(examinationTypeRepositoryMocked.findAll()).thenReturn(types);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO(null, SURNAME, null,
                "2020-05-05", "10:00", "11:00", null, 8L, null);

        List<DoctorSearchListDTO> retVal = this.clinicService.searchDoctorsPatient(dto);

        Assert.assertNotNull(retVal);
        Assert.assertEquals(1, retVal.size());
        Assert.assertEquals(doctor1.getId(), retVal.get(0).id);

        verify(userRepositoryMocked, times(0)).findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class));
        verify(userRepositoryMocked, times(1)).findDoctorsOfClinic(any(Long.class));
        verify(doctorAbsenceRepositoryMocked, times(4)).findByStaff(any(Long.class));
        verify(examinationTypeRepositoryMocked, times(0)).findAll();
    }

    @Test
    public void testSearchDoctorsPatientThrowsException() throws Exception {

        Doctor doctor1 = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME2, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor2 = new Doctor(DOCTOR_EMAIL2, PASSWORD, NAME2, SURNAME2, PHONE, 5L, 4.5, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor3 = new Doctor(DOCTOR_EMAIL3, PASSWORD, NAME3, SURNAME3, PHONE, 2L, 4.0, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        Doctor doctor4 = new Doctor(DOCTOR_EMAIL4, PASSWORD, NAME4, SURNAME4, PHONE, 20L, 3.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor1.setId(1L);
        doctor2.setId(2L);
        doctor3.setId(3L);
        doctor4.setId(4L);
        Clinic clinic = new Clinic();
        clinic.setId(8L);
        doctor1.setClinic(clinic);
        doctor2.setClinic(clinic);
        doctor3.setClinic(clinic);
        doctor4.setClinic(clinic);
        ExaminationType type1 = new ExaminationType("Pregled ociju", 100);
        ExaminationType type2 = new ExaminationType("Pregled usiju", 100);
        ExaminationType type3 = new ExaminationType("Pregled usta", 100);
        type1.setId(5L);
        type1.setId(6L);
        type1.setId(7L);
        doctor1.setSpecialty(type1);
        doctor2.setSpecialty(type1);
        doctor3.setSpecialty(type2);
        doctor4.setSpecialty(type3);

        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctor1);
        doctors.add(doctor2);
        doctors.add(doctor3);
        doctors.add(doctor4);

        List<ExaminationType> types = new ArrayList<>();
        types.add(type1);
        types.add(type2);
        types.add(type3);

        List<Doctor> doctorsType1 = new ArrayList<>();
        doctorsType1.add(doctor1);
        doctorsType1.add(doctor2);

        Mockito.when(userRepositoryMocked.findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class))).thenReturn(doctorsType1);
        Mockito.when(userRepositoryMocked.findDoctorsOfClinic(any(Long.class))).thenReturn(doctors);
        Mockito.when(doctorAbsenceRepositoryMocked.findByStaff(any(Long.class))).thenReturn(new ArrayList<>());
        Mockito.when(examinationTypeRepositoryMocked.findAll()).thenReturn(types);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO(null, null, null,
                "35-62-2020", "26:00", "11:00", null, 8L, "Pregled ociju");
        List<DoctorSearchListDTO> retVal = null;
        try {
            retVal = this.clinicService.searchDoctorsPatient(dto);
            Assert.fail();
        }catch (DateTimeParseException e) {
            Assert.assertNull(retVal);
        }

        verify(userRepositoryMocked, times(1)).findDoctorsOfClinicBySpecialty(any(Long.class), any(Long.class));
        verify(userRepositoryMocked, times(0)).findDoctorsOfClinic(any(Long.class));
        verify(doctorAbsenceRepositoryMocked, times(1)).findByStaff(any(Long.class));
        verify(examinationTypeRepositoryMocked, times(1)).findAll();
    }
}
