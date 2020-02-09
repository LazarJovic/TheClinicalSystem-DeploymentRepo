package com.example.clinicalCenter.controllers;

import com.example.clinicalCenter.TestUtils;
import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.ExaminationRequestDTO;
import com.example.clinicalCenter.model.Clinic;
import com.example.clinicalCenter.model.Doctor;
import com.example.clinicalCenter.model.ExaminationType;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static com.example.clinicalCenter.constants.ClinicConstants.CLINIC_ID;
import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.RoomConstants.ROOM_ID;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ExaminationRequestControllerIntegrationTests {

    private static final String URL_PREFIX = "/api/examination-requests";

    private String accessToken;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void rollback() {
        Resource resource = new ClassPathResource("data-h2.sql");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(resource);
        resourceDatabasePopulator.execute(dataSource);
    }

    @Before
    public void onSetUp() {
        Doctor doctor = new Doctor(DOCTOR_EMAIL, PASSWORD, NAME, SURNAME, PHONE, 10L, 4.8, DOCTOR_START_SHIFT, DOCTOR_END_SHIFT);
        doctor.setId(6L);
        Clinic clinic = new Clinic("Blagoja Parovica","Novi Sad","Fina klinika", "Gradska bolnica", 5L, 4.8);
        clinic.setId(2L);
        doctor.setClinic(clinic);
        ExaminationType type = new ExaminationType("Tip1", 50);
        type.setId(10L);
        doctor.setSpecialty(type);
        Authentication auth = new UsernamePasswordAuthenticationToken(doctor,null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void login(String username, String password) {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/auth/login",
                new JwtAuthenticationRequest(username,password), UserTokenState.class);
        accessToken = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    @Test
    public void testCreateExaminationRequest() throws Exception {
        login("doctor1@maildrop.cc", "123456789");

        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.examDate").value("2020-04-01"))
                .andExpect(jsonPath("$.startTime").value("12:00"))
                .andExpect(jsonPath("$.endTime").value("13:00"))
                .andExpect(jsonPath("$.type").value("10"))
                .andExpect(jsonPath("$.doctor").value("6"))
                .andExpect(jsonPath("$.patient").value("4"))
                .andExpect(jsonPath("$.clinic").value("2"));
    }

    @Test
    public void testCreateExaminationRequestBadRequest() throws Exception {
        login("doctor1@maildrop.cc", "123456789");

        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-15-15", "12:00", "13:00", 10L, 4L);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateExaminationRequestPatient_Success() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(EXAMINATION_REQUEST_ID, EXAM_DATE, START_TIME, END_TIME,
                EXAMINATION_TYPE_ID, DOCTOR_ID, CLINIC_ID, 0L, PATIENT_ID);

        String requestJson = TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX + "/create-examination-request")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.examDate").value(EXAM_DATE))
                .andExpect(jsonPath("$.startTime").value(START_TIME))
                .andExpect(jsonPath("$.endTime").value(END_TIME))
                .andExpect(jsonPath("$.type").value(EXAMINATION_TYPE_ID))
                .andExpect(jsonPath("$.doctor").value(DOCTOR_ID))
                .andExpect(jsonPath("$.clinic").value(CLINIC_ID))
                .andExpect(jsonPath("$.room").value(0))
                .andExpect(jsonPath("$.patient").value(PATIENT_ID));
    }

    @Test
    public void testCreateExaminationRequestPatient_BadRequest_WrongDateFormat() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(EXAMINATION_REQUEST_ID, BAD_DATE_FORMAT, START_TIME, END_TIME,
                EXAMINATION_TYPE_ID, DOCTOR_ID, CLINIC_ID, ROOM_ID, PATIENT_ID);

        String requestJson = TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX + "/create-examination-request")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson));
    }
}
