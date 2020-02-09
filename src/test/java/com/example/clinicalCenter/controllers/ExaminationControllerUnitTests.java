package com.example.clinicalCenter.controllers;

import com.example.clinicalCenter.TestUtils;
import com.example.clinicalCenter.dto.ExaminationDTO;
import com.example.clinicalCenter.dto.PredefinedExaminationDTO;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import com.example.clinicalCenter.service.ExaminationService;
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

import static com.example.clinicalCenter.constants.ExaminationConstants.*;
import static com.example.clinicalCenter.constants.UserConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class ExaminationControllerUnitTests {

    public static final String URL_PREFIX = "/api/examinations";

    private String accessToken;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    @MockBean
    private ExaminationService examinationServiceMocked;

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
    public void testGetExaminationById() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);
        Mockito.when(examinationServiceMocked.findOne((long)1)).thenReturn(new ExaminationDTO((long)1, (long)1, "", "", "", "5",
                (long)1, (long)1, (long)1, (long)1));

        mockMvc.perform(get(URL_PREFIX + "/1").
                header("Authorization", accessToken)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.id").value((long)1)).
                andExpect(jsonPath("$.discount").value("5"));
    }

    @Test
    public void testSchedulePredefinedExaminationSuccess() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);

        PredefinedExaminationDTO dto = new PredefinedExaminationDTO(EXAMINATION_ID, TYPE_NAME, EXAM_DATE, START_TIME, END_TIME,
                PRICE_WITH_DISCOUNT, DOCTOR_NAME, NURSE_NAME, ROOM_NAME);

        Mockito.when(examinationServiceMocked.schedule(any(PredefinedExaminationDTO.class))).thenReturn(dto);

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

        verify(examinationServiceMocked, times(1))
                .schedule(any(PredefinedExaminationDTO.class));
    }

    @Test
    public void testSchedulePredefinedExaminationBadRequest() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);

        PredefinedExaminationDTO dto = new PredefinedExaminationDTO(EXAMINATION_ID, TYPE_NAME, BAD_DATE_FORMAT, START_TIME, END_TIME,
                PRICE_WITH_DISCOUNT, DOCTOR_NAME, NURSE_NAME, ROOM_NAME);

        Mockito.when(examinationServiceMocked.schedule(any(PredefinedExaminationDTO.class))).thenReturn(null);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(put(URL_PREFIX + "/schedule-examination")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(examinationServiceMocked, times(1))
                .schedule(any(PredefinedExaminationDTO.class));
    }

    @Test
    public void testCreateExaminationSuccess() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);

        ExaminationDTO dto = new ExaminationDTO(NEW_EXAMINATION_ID, 10L, EXAM_DATE, START_TIME, END_TIME, "0", 6L,
                7L, 11L, 4L);

        Mockito.when(examinationServiceMocked.create(any(ExaminationDTO.class))).thenReturn(dto);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(NEW_EXAMINATION_ID))
                .andExpect(jsonPath("$.type").value(10L))
                .andExpect(jsonPath("$.examDate").value(EXAM_DATE))
                .andExpect(jsonPath("$.startTime").value(START_TIME))
                .andExpect(jsonPath("$.endTime").value(END_TIME))
                .andExpect(jsonPath("$.discount").value("0"))
                .andExpect(jsonPath("$.doctor").value(6L))
                .andExpect(jsonPath("$.nurse").value(7L))
                .andExpect(jsonPath("$.room").value(11L))
                .andExpect(jsonPath("$.patient").value(4L));

        verify(examinationServiceMocked, times(1))
                .create(any(ExaminationDTO.class));
    }

    @Test
    public void testCreateExaminationBadRequestWrongDateFormat() throws Exception {
        login(CLINIC_ADMIN_EMAIL, PASSWORD);

        ExaminationDTO dto = new ExaminationDTO(NEW_EXAMINATION_ID, 10L, BAD_DATE_FORMAT, START_TIME, END_TIME, "0", 6L,
                7L, 11L, 4L);

        Mockito.when(examinationServiceMocked.create(any(ExaminationDTO.class))).thenReturn(null);

        String requestJson =  TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(examinationServiceMocked, times(1))
                .create(any(ExaminationDTO.class));
    }

}
