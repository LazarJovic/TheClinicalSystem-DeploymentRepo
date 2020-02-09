package com.example.clinicalCenter.controllers;

import com.example.clinicalCenter.TestUtils;
import com.example.clinicalCenter.dto.ExaminationDTO;
import com.example.clinicalCenter.dto.PredefinedExaminationDTO;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
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

import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ExaminationControllerIntegrationTests {

    public static final String URL_PREFIX = "/api/examinations";

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

    public void login(String username, String password) {
        ResponseEntity<UserTokenState> responseEntity = restTemplate.postForEntity("/auth/login",
                new JwtAuthenticationRequest(username,password), UserTokenState.class);
        accessToken = "Bearer " + responseEntity.getBody().getAccessToken();
    }

    public void onSetUpLoginPatient() {
        Patient patient = new Patient(4L, PATIENT_EMAIL, "123456789", "Sima", "Simic", "Stari Most",
                "Sremska Mitrovica", "Srbija", "12345670", "12345", null);

        Authentication auth = new UsernamePasswordAuthenticationToken(patient,null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testSchedulePredefinedExaminationSuccess() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);
        onSetUpLoginPatient();
        PredefinedExaminationDTO dto = new PredefinedExaminationDTO(EXAMINATION_ID, TYPE_NAME, EXAM_DATE, START_TIME, END_TIME,
                PRICE_WITH_DISCOUNT, DOCTOR_NAME, NURSE_NAME, ROOM_NAME);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(put(URL_PREFIX + "/schedule-examination")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(EXAMINATION_ID))
                .andExpect(jsonPath("$.typeName").value(TYPE_NAME))
                .andExpect(jsonPath("$.examDate").value(EXAM_DATE))
                .andExpect(jsonPath("$.startTime").value(START_TIME))
                .andExpect(jsonPath("$.endTime").value(END_TIME))
                .andExpect(jsonPath("$.priceWithDiscount").value(PRICE_WITH_DISCOUNT))
                .andExpect(jsonPath("$.doctorName").value(DOCTOR_NAME))
                .andExpect(jsonPath("$.nurseName").value(NURSE_NAME))
                .andExpect(jsonPath("$.roomName").value(ROOM_NAME));
    }

    @Test
    public void testCreateExaminationSuccess() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);
        ExaminationDTO dto = new ExaminationDTO(null, 0L, EXAM_DATE, START_TIME, END_TIME, "0", 6L,
                14L, 11L, 4L);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.type").value(10L))
                .andExpect(jsonPath("$.examDate").value(EXAM_DATE))
                .andExpect(jsonPath("$.startTime").value(START_TIME))
                .andExpect(jsonPath("$.endTime").value(END_TIME))
                .andExpect(jsonPath("$.discount").value("0.0"))
                .andExpect(jsonPath("$.doctor").value(6L))
                .andExpect(jsonPath("$.nurse").value(3L))
                .andExpect(jsonPath("$.room").value(11L))
                .andExpect(jsonPath("$.patient").value(4L));
    }

    @Test
    public void testCreateExaminationBadRequestWrongDateFormat() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);
        ExaminationDTO dto = new ExaminationDTO(null, 0L, BAD_DATE_FORMAT, START_TIME, END_TIME, "0", 6L,
                14L, 11L, 4L);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateExaminationBadRequestWrongTimeFormat() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);
        ExaminationDTO dto = new ExaminationDTO(null, 0L, EXAM_DATE, BAD_TIME_FORMAT, END_TIME, "0", 6L,
                14L, 11L, 4L);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateExaminationBadRequestWrongDoubleFormat() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);
        ExaminationDTO dto = new ExaminationDTO(null, 0L, EXAM_DATE, START_TIME, END_TIME, BAD_DISCOUNT_FORMAT, 6L,
                14L, 11L, 4L);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

}
