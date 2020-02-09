package com.example.clinicalCenter.controllers;

import com.example.clinicalCenter.TestUtils;
import com.example.clinicalCenter.dto.SearchClinicDTO;
import com.example.clinicalCenter.dto.SearchDoctorPatientDTO;
import com.example.clinicalCenter.model.Patient;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.example.clinicalCenter.constants.ClinicConstants.CLINIC_ID;
import static com.example.clinicalCenter.constants.ClinicConstants.SEARCH_CLINIC_COUNT;
import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ClinicControllerIntegrationTests {

    public static final String URL_PREFIX = "/api/clinics";

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
    public void testSearchDoctorsPatient1() throws Exception {
        login("patient1@maildrop.cc", "123456789");

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO(null, null, null,
                null, null, null, null, 2L, "Ocni pregled");

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(6)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(13)));
    }

    @Test
    public void testSearchDoctorsPatient2() throws Exception {
        login("patient1@maildrop.cc", "123456789");

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO(null, SURNAME, null,
                "2020-05-05", "10:00", "11:00", null, 2L, null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(13)));
    }

    @Test
    public void testSearchDoctorsPatientBadRequest() throws Exception {
        login("patient1@maildrop.cc", "123456789");

        SearchDoctorPatientDTO dto = new SearchDoctorPatientDTO(null, SURNAME, null,
                "2020-15-15", "10:00", "11:00", null, 2L, null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX + "/search-doctors-patient")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchClinicSuccess() throws Exception {

        login(PATIENT_EMAIL, PASSWORD);

        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, "",
                null, "");

        String requestJson = TestUtils.json(searchClinicDTO);

        mockMvc.perform(post(URL_PREFIX + "/search-clinics")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(SEARCH_CLINIC_COUNT)))
                .andExpect(jsonPath("$.[*].id").value(hasItem(CLINIC_ID.intValue())));
    }

    @Test
    public void testSearchClinicsBadRequestWrongDateFormat() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);
        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(BAD_DATE_FORMAT, START_TIME, END_TIME, "",
                null, null);

        String requestJson =  TestUtils.json(searchClinicDTO);

        mockMvc.perform(post(URL_PREFIX + "/search-clinics")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSearchClinicNoTypeInTheSystem() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);
        SearchClinicDTO searchClinicDTO = new SearchClinicDTO(EXAM_DATE, START_TIME, END_TIME, "",
                null, "Pregled");

        String requestJson =  TestUtils.json(searchClinicDTO);

        mockMvc.perform(post(URL_PREFIX + "/search-clinics")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

}
