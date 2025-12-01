package com.gestor_empresarial.registration_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestor_empresarial.registration_service.dtos.RegistrationRequestDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationResponseDto;
import com.gestor_empresarial.registration_service.dtos.UpdateRegistrationStatusDto;
import com.gestor_empresarial.registration_service.enums.RegistrationStatus;
import com.gestor_empresarial.registration_service.services.RegistrationService;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistrationRequestDto registrationRequest;
    private RegistrationResponseDto registrationResponse;
    private UpdateRegistrationStatusDto updateRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequestDto(1L);
        registrationResponse = new RegistrationResponseDto(1L, 1L, 1L, RegistrationStatus.PENDING_PAYMENT);
        updateRequest = new UpdateRegistrationStatusDto(RegistrationStatus.CONFIRMED);
    }

    @Test
    void createRegistration_ShouldReturnOk() throws Exception {
        when(registrationService.createRegistration(any(RegistrationRequestDto.class), eq(1L)))
                .thenReturn(registrationResponse);

        mockMvc.perform(post("/api/registrations")
                .header("X-User-ID", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1L));
    }

    @Test
    void getAllRegistrationsByUserId_ShouldReturnList() throws Exception {
        when(registrationService.findAllByUserId(1L)).thenReturn(List.of(registrationResponse));

        mockMvc.perform(get("/api/registrations/user")
                .header("X-User-ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllByEventID_ShouldReturnList() throws Exception {
        when(registrationService.findAllByEventId(1L)).thenReturn(List.of(registrationResponse));

        mockMvc.perform(get("/api/registrations/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateRegistrationStatus_ShouldReturnOk() throws Exception {
        when(registrationService.updateRegistrationStatus(eq(1L), eq(1L), any(UpdateRegistrationStatusDto.class)))
                .thenReturn(registrationResponse);

        mockMvc.perform(put("/api/registrations/1")
                .header("X-User-ID", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
    }

    @Test
    void deleteRegistration_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/registrations/1")
                .header("X-User-ID", 1L))
                .andExpect(status().isNoContent());
    }
}
