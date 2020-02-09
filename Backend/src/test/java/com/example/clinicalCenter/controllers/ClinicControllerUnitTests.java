package com.example.clinicalCenter.controllers;

import com.example.clinicalCenter.dto.ClinicSearchListDTO;
import com.example.clinicalCenter.dto.DoctorSearchListDTO;
import com.example.clinicalCenter.dto.SearchClinicDTO;
import com.example.clinicalCenter.dto.SearchDoctorPatientDTO;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import com.example.clinicalCenter.service.ClinicService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.example.clinicalCenter.constants.ClinicConstants.*;
import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ClinicControllerUnitTests {

    public static final String URL_PREFIX = "/api/clinics";

    private String accessToken;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicServiceMocked;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PostConstruct
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    public void login(String username, String password) {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/auth/login",
                new JwtAuthenticationRequest(username,password), UserTokenState.class);
        accessToken = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testSearchClinicsSuccess() throws Exception {

        login(PATIENT_EMAIL, PASSWORD);

        ClinicSearchListDTO dto1 = new ClinicSearchListDTO(CLINIC_ID, CLINIC_NAME, CLINIC_ADDRESS,
                                                            CLINIC_CITY, RATING, EXAMINATION_PRICE);
        ClinicSearchListDTO dto2 = new ClinicSearchListDTO(NEW_CLINIC_ID, NEW_CLINIC_NAME, CLINIC_ADDRESS,
                                                            CLINIC_CITY, RATING, EXAMINATION_PRICE);
        List<ClinicSearchListDTO> retVal = new ArrayList<>();
        retVal.add(dto1);
        retVal.add(dto2);

        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, "",
                                                        null, null);

        Mockito.when(this.clinicServiceMocked.searchClinic(any(SearchClinicDTO.class))).thenReturn(retVal);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(searchClinicDTO);

        mockMvc.perform(post(URL_PREFIX + "/search-clinics")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(SEARCH_CLINIC_COUNT)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(CLINIC_ID.intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(CLINIC_NAME)))
                .andExpect(jsonPath("$.[*].address").value(hasItem(CLINIC_ADDRESS)))
                .andExpect(jsonPath("$.[*].city").value(hasItem(CLINIC_CITY)))
                .andExpect(jsonPath("$.[*].rating").value(hasItem(RATING)))
                .andExpect(jsonPath("$.[*].examinationPrice").value(hasItem(EXAMINATION_PRICE)));

        verify(this.clinicServiceMocked, times(1)).searchClinic(any(SearchClinicDTO.class));
    }

    @Test
    public void testSearchDoctorsPatient() throws Exception {
        login("patient1@maildrop.cc", "123456789");

        List<DoctorSearchListDTO> results = new ArrayList<>();
        DoctorSearchListDTO doctor1 = new DoctorSearchListDTO(1L, NAME, SURNAME, "4.8", "", "", "");
        DoctorSearchListDTO doctor2 = new DoctorSearchListDTO(2L, NAME2, SURNAME2, "5.0", "", "", "");
        DoctorSearchListDTO doctor3 = new DoctorSearchListDTO(3L, NAME3, SURNAME3, "4.5", "", "", "");
        DoctorSearchListDTO doctor4 = new DoctorSearchListDTO(4L, NAME4, SURNAME4, "3.5", "", "", "");
        results.add(doctor1);
        results.add(doctor2);
        results.add(doctor3);
        results.add(doctor4);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO();

        Mockito.when(clinicServiceMocked.searchDoctorsPatient(any(SearchDoctorPatientDTO.class))).thenReturn(results);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(4)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(doctor1.id.intValue())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(doctor2.id.intValue())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(doctor3.id.intValue())))
                .andExpect(jsonPath("$.[*].id").value(hasItem(doctor4.id.intValue())));

        verify(clinicServiceMocked, times(1)).searchDoctorsPatient(any(SearchDoctorPatientDTO.class));
    }

    @Test
    public void testSearchClinicsBadRequestWrongDateFormat() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);
        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(BAD_DATE_FORMAT, START_TIME, END_TIME, "",
                null, null);

        Mockito.when(clinicServiceMocked.searchClinic(any(SearchClinicDTO.class))).thenReturn(null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(searchClinicDTO);

        mockMvc.perform(post(URL_PREFIX + "/search-clinics")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(clinicServiceMocked, times(1))
                .searchClinic(any(SearchClinicDTO.class));
    }
       
    @Test
    public void testSearchDoctorsPatientBadRequestException() throws Exception {
        login("patient1@maildrop.cc", "123456789");

        List<DoctorSearchListDTO> results = new ArrayList<>();
        DoctorSearchListDTO doctor1 = new DoctorSearchListDTO(1L, NAME, SURNAME, "4.8", "", "", "");
        DoctorSearchListDTO doctor2 = new DoctorSearchListDTO(2L, NAME2, SURNAME2, "5.0", "", "", "");
        DoctorSearchListDTO doctor3 = new DoctorSearchListDTO(3L, NAME3, SURNAME3, "4.5", "", "", "");
        DoctorSearchListDTO doctor4 = new DoctorSearchListDTO(4L, NAME4, SURNAME4, "3.5", "", "", "");
        results.add(doctor1);
        results.add(doctor2);
        results.add(doctor3);
        results.add(doctor4);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO();

        Mockito.when(clinicServiceMocked.searchDoctorsPatient(any(SearchDoctorPatientDTO.class))).thenThrow(new Exception("Error message"));//thenReturn(results);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Error message"));

        verify(clinicServiceMocked, times(1)).searchDoctorsPatient(any(SearchDoctorPatientDTO.class));
    }

    @Test
    public void testSearchDoctorsPatientForbiddenException() throws Exception {
        login("doctor1@maildrop.cc", "123456789");

        List<DoctorSearchListDTO> results = new ArrayList<>();
        DoctorSearchListDTO doctor1 = new DoctorSearchListDTO(1L, NAME, SURNAME, "4.8", "", "", "");
        DoctorSearchListDTO doctor2 = new DoctorSearchListDTO(2L, NAME2, SURNAME2, "5.0", "", "", "");
        DoctorSearchListDTO doctor3 = new DoctorSearchListDTO(3L, NAME3, SURNAME3, "4.5", "", "", "");
        DoctorSearchListDTO doctor4 = new DoctorSearchListDTO(4L, NAME4, SURNAME4, "3.5", "", "", "");
        results.add(doctor1);
        results.add(doctor2);
        results.add(doctor3);
        results.add(doctor4);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO();

        Mockito.when(clinicServiceMocked.searchDoctorsPatient(any(SearchDoctorPatientDTO.class))).thenReturn(results);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isForbidden());

        verify(clinicServiceMocked, times(0)).searchDoctorsPatient(any(SearchDoctorPatientDTO.class));
    }

    @Test
    public void testSearchDoctorsPatientUnauthorizedException() throws Exception {
        List<DoctorSearchListDTO> results = new ArrayList<>();
        DoctorSearchListDTO doctor1 = new DoctorSearchListDTO(1L, NAME, SURNAME, "4.8", "", "", "");
        DoctorSearchListDTO doctor2 = new DoctorSearchListDTO(2L, NAME2, SURNAME2, "5.0", "", "", "");
        DoctorSearchListDTO doctor3 = new DoctorSearchListDTO(3L, NAME3, SURNAME3, "4.5", "", "", "");
        DoctorSearchListDTO doctor4 = new DoctorSearchListDTO(4L, NAME4, SURNAME4, "3.5", "", "", "");
        results.add(doctor1);
        results.add(doctor2);
        results.add(doctor3);
        results.add(doctor4);

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO();

        Mockito.when(clinicServiceMocked.searchDoctorsPatient(any(SearchDoctorPatientDTO.class))).thenReturn(results);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isUnauthorized());

        verify(clinicServiceMocked, times(0)).searchDoctorsPatient(any(SearchDoctorPatientDTO.class));
    }
}
