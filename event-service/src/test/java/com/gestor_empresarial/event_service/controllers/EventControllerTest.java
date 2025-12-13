package com.gestor_empresarial.event_service.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import com.gestor_empresarial.event_service.dtos.EventRequestDto;
import com.gestor_empresarial.event_service.dtos.EventResponseDto;
import com.gestor_empresarial.event_service.dtos.EventStatusUpdateDTO;
import com.gestor_empresarial.event_service.dtos.EventUpdateRequestDto;
import com.gestor_empresarial.event_service.enums.Status;
import com.gestor_empresarial.event_service.services.EventService;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EventService eventService;

        @Autowired
        private ObjectMapper objectMapper;

        private EventRequestDto eventRequest;
        private EventResponseDto eventResponse;
        private EventUpdateRequestDto updateRequest;
        private EventStatusUpdateDTO statusUpdate;

        @BeforeEach
        void setUp() {
                eventRequest = new EventRequestDto("Test Event", "Description", "Location", LocalDateTime.now(),
                                LocalDateTime.now().plusHours(2), 100, true);
                eventResponse = new EventResponseDto(1L, "Test Event", "Description", 1L, "Location",
                                LocalDateTime.now(),
                                LocalDateTime.now().plusHours(2), 100, 0, true, "ACTIVE");
                updateRequest = new EventUpdateRequestDto("Updated Event", "Description", "Location",
                                LocalDateTime.now(),
                                LocalDateTime.now().plusHours(2), 100, true);
                statusUpdate = new EventStatusUpdateDTO(Status.COMPLETED);
        }

        @Test
        void createEvent_ShouldReturnOk() throws Exception {
                when(eventService.createEvent(any(EventRequestDto.class), eq(1L))).thenReturn(eventResponse);

                mockMvc.perform(post("/api/events")
                                .header("X-User-ID", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(eventRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Test Event"));
        }

        @Test
        void getPublishedEventsByTitle_ShouldReturnList() throws Exception {
                when(eventService.findPublishedEventsByTitle("Test")).thenReturn(List.of(eventResponse));

                mockMvc.perform(get("/api/events/title/Test"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        void getPublishedEventById_ShouldReturnEvent() throws Exception {
                when(eventService.findPublishedEventById(1L)).thenReturn(eventResponse);

                mockMvc.perform(get("/api/events/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Test Event"));
        }

        @Test
        void getEventsByOrganizerId_ShouldReturnList() throws Exception {
                when(eventService.getAllEventsByOrganizerId(1L)).thenReturn(List.of(eventResponse));

                mockMvc.perform(get("/api/events/organizer/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        void getCreatedEventsByLoggedUser_ShouldReturnList() throws Exception {
                when(eventService.getAllEventsByOrganizerId(1L)).thenReturn(List.of(eventResponse));

                mockMvc.perform(get("/api/events/mine")
                                .header("X-User-ID", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        void updateEvent_ShouldReturnOk() throws Exception {
                when(eventService.updateEvent(eq(1L), eq(1L), any(EventUpdateRequestDto.class)))
                                .thenReturn(eventResponse);

                mockMvc.perform(put("/api/events/1")
                                .header("X-User-ID", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Test Event"));
        }

        @Test
        void deleteEvent_ShouldReturnNoContent() throws Exception {
                mockMvc.perform(delete("/api/events/1")
                                .header("X-User-ID", 1L))
                                .andExpect(status().isNoContent());
        }

        @Test
        void updateEventStatus_ShouldReturnOk() throws Exception {
                when(eventService.updateEventStatus(eq(1L), eq(1L), any(EventStatusUpdateDTO.class)))
                                .thenReturn(eventResponse);

                mockMvc.perform(put("/api/events/1/status")
                                .header("X-User-ID", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(statusUpdate)))
                                .andExpect(status().isOk());
        }

        @Test
        void reserveCapacityAndRegister_ShouldReturnNoContent() throws Exception {
                mockMvc.perform(post("/api/events/register/1")
                                .header("X-User-ID", 1L))
                                .andExpect(status().isNoContent());
        }

        @Test
        void releaseCapacity_ShouldReturnNoContent() throws Exception {
                mockMvc.perform(post("/api/events/release/1")
                                .header("X-User-ID", 1L))
                                .andExpect(status().isNoContent());
        }
}
