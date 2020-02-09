package com.example.clinicalCenter.controllers;

import com.example.clinicalCenter.TestUtils;
import com.example.clinicalCenter.dto.CreateAppointmentDTO;
import com.example.clinicalCenter.dto.ExaminationRequestDTO;
import com.example.clinicalCenter.model.UserTokenState;
import com.example.clinicalCenter.security.TokenUtils;
import com.example.clinicalCenter.security.auth.JwtAuthenticationRequest;
import com.example.clinicalCenter.service.ExaminationRequestService;
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
public class ExaminationRequestControllerUnitTests {

    public static final String URL_PREFIX = "/api/examination-requests";

    private String accessToken;

    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    private MockMvc mockMvc;

    @MockBean
    private ExaminationRequestService examinationRequestServiceMocked;

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
    public void testCreateExaminationRequestDoctor() throws Exception {
        login("doctor1@maildrop.cc", "123456789");

        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);
        ExaminationRequestDTO retVal = new ExaminationRequestDTO(50L, "2020-04-01", "12:00", "13:00", 10L, 6L, 2L, 11L, 4L);

        Mockito.when(examinationRequestServiceMocked.createExaminationRequest(any(CreateAppointmentDTO.class))).thenReturn(retVal);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(50L));

        verify(examinationRequestServiceMocked, times(1)).createExaminationRequest(any(CreateAppointmentDTO.class));
    }

    @Test
    public void testCreateExaminationRequestDoctorBadRequest() throws Exception {
        login("doctor1@maildrop.cc", "123456789");

        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);

        Mockito.when(examinationRequestServiceMocked.createExaminationRequest(any(CreateAppointmentDTO.class))).thenThrow(new Exception("Error message"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Error message"));

        verify(examinationRequestServiceMocked, times(1)).createExaminationRequest(any(CreateAppointmentDTO.class));
    }

    @Test
    public void testCreateExaminationRequestDoctorForbidden() throws Exception {
        login("patient1@maildrop.cc", "123456789");

        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);
        ExaminationRequestDTO retVal = new ExaminationRequestDTO(50L, "2020-04-01", "12:00", "13:00", 10L, 6L, 2L, 11L, 4L);

        Mockito.when(examinationRequestServiceMocked.createExaminationRequest(any(CreateAppointmentDTO.class))).thenReturn(retVal);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX)
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isForbidden());

        verify(examinationRequestServiceMocked, times(0)).createExaminationRequest(any(CreateAppointmentDTO.class));
    }

    @Test
    public void testCreateExaminationRequestDoctorUnauthorized() throws Exception {
        CreateAppointmentDTO dto = new CreateAppointmentDTO("2020-04-01", "12:00", "13:00", 10L, 4L);
        ExaminationRequestDTO retVal = new ExaminationRequestDTO(50L, "2020-04-01", "12:00", "13:00", 10L, 6L, 2L, 11L, 4L);

        Mockito.when(examinationRequestServiceMocked.createExaminationRequest(any(CreateAppointmentDTO.class))).thenReturn(retVal);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String requestJson =  mapper.writeValueAsString(dto);

        mockMvc.perform(post(URL_PREFIX)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isUnauthorized());

        verify(examinationRequestServiceMocked, times(0)).createExaminationRequest(any(CreateAppointmentDTO.class));
    }

    @Test
    public void testCreateExaminationRequestPatient_Success() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(EXAMINATION_REQUEST_ID, EXAM_DATE, START_TIME, END_TIME,
                EXAMINATION_TYPE_ID, DOCTOR_ID, CLINIC_ID, ROOM_ID, PATIENT_ID);

        ExaminationRequestDTO retVal = new ExaminationRequestDTO(EXAMINATION_REQUEST_ID, EXAM_DATE, START_TIME, END_TIME,
                EXAMINATION_TYPE_ID, DOCTOR_ID, CLINIC_ID, ROOM_ID, PATIENT_ID);

        Mockito.when(this.examinationRequestServiceMocked.createExaminationRequestPatient(any(ExaminationRequestDTO.class))).thenReturn(retVal);

        String requestJson = TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX + "/create-examination-request")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id").value(EXAMINATION_REQUEST_ID));

        verify(this.examinationRequestServiceMocked, times(1)).createExaminationRequestPatient(any(ExaminationRequestDTO.class));
    }

    @Test
    public void testCreateExaminationRequestPatient_BadRequest_WrongDateFormat() throws Exception {
        login(PATIENT_EMAIL, PASSWORD);

        ExaminationRequestDTO dto = new ExaminationRequestDTO(EXAMINATION_REQUEST_ID, BAD_DATE_FORMAT, START_TIME, END_TIME,
                EXAMINATION_TYPE_ID, DOCTOR_ID, CLINIC_ID, ROOM_ID, PATIENT_ID);

        Mockito.when(this.examinationRequestServiceMocked.createExaminationRequestPatient(any(ExaminationRequestDTO.class))).thenReturn(null);

        String requestJson = TestUtils.json(dto);

        mockMvc.perform(post(URL_PREFIX + "/create-examination-request")
                .header("Authorization", accessToken)
                .contentType(contentType)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(this.examinationRequestServiceMocked, times(1)).createExaminationRequestPatient(any(ExaminationRequestDTO.class));
    }
}
