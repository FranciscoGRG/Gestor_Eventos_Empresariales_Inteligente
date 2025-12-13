package com.gestor_empresarial.registration_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.gestor_empresarial.registration_service.clients.IEventFeignClient;
import com.gestor_empresarial.registration_service.clients.IUserFeignClient;
import com.gestor_empresarial.registration_service.dtos.RegistrationRequestDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationResponseDto;
import com.gestor_empresarial.registration_service.dtos.UpdateRegistrationStatusDto;
import com.gestor_empresarial.registration_service.enums.RegistrationStatus;
import com.gestor_empresarial.registration_service.exceptions.AlreadyRegisteredException;
import com.gestor_empresarial.registration_service.exceptions.RegistrationNotFoundException;
import com.gestor_empresarial.registration_service.models.RegistrationModel;
import com.gestor_empresarial.registration_service.repositories.IRegistrationRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private IRegistrationRepository repository;

    @Mock
    private IEventFeignClient eventClient;

    @Mock
    private IUserFeignClient userClient;

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationModel registration;
    private RegistrationRequestDto registrationRequest;
    private UpdateRegistrationStatusDto updateRequest;

    @BeforeEach
    void setUp() {
        registration = new RegistrationModel();
        registration.setId(1L);
        registration.setUserId(1L);
        registration.setEventId(1L);
        registration.setStatus(RegistrationStatus.PENDING_PAYMENT);

        registrationRequest = new RegistrationRequestDto(1L);
        updateRequest = new UpdateRegistrationStatusDto(RegistrationStatus.CONFIRMED);
    }

    @Test
    void createRegistration_ShouldReturnResponse_WhenNotRegistered() {
        when(repository.existsByEventIdAndUserId(1L, 1L)).thenReturn(false);
        when(eventClient.reserveCapacityAndRegister(1L, 1L)).thenReturn(ResponseEntity.noContent().build());
        when(repository.save(any(RegistrationModel.class))).thenReturn(registration);

        // Mock User and Event clients responses
        com.gestor_empresarial.registration_service.dtos.UserDto userDto = new com.gestor_empresarial.registration_service.dtos.UserDto(
                "john@example.com", "John Doe");
        when(userClient.getUserNameAndEmail(1L)).thenReturn(ResponseEntity.ok(userDto));

        com.gestor_empresarial.registration_service.dtos.EventDto eventDto = new com.gestor_empresarial.registration_service.dtos.EventDto(
                1L,
                "Test Event",
                "Desc",
                1L,
                "Loc",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusHours(2),
                100,
                0,
                true,
                "ACTIVE");
        when(eventClient.getEventById(1L)).thenReturn(ResponseEntity.ok(eventDto));

        RegistrationResponseDto response = registrationService.createRegistration(registrationRequest, 1L);

        assertNotNull(response);
        assertEquals(1L, response.eventId());
        verify(eventClient).reserveCapacityAndRegister(1L, 1L);
        verify(repository).save(any(RegistrationModel.class));
    }

    @Test
    void createRegistration_ShouldThrowException_WhenAlreadyRegistered() {
        when(repository.existsByEventIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(AlreadyRegisteredException.class,
                () -> registrationService.createRegistration(registrationRequest, 1L));
        verify(eventClient, never()).reserveCapacityAndRegister(anyLong(), anyLong());
    }

    @Test
    void findAllByUserId_ShouldReturnList() {
        when(repository.findAllByUserId(1L)).thenReturn(List.of(registration));

        List<RegistrationResponseDto> responses = registrationService.findAllByUserId(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void findAllByEventId_ShouldReturnList() {
        when(repository.findAllByEventId(1L)).thenReturn(List.of(registration));

        List<RegistrationResponseDto> responses = registrationService.findAllByEventId(1L);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void updateRegistrationStatus_ShouldUpdate_WhenExists() {
        when(repository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(registration));
        when(repository.save(any(RegistrationModel.class))).thenReturn(registration);

        RegistrationResponseDto response = registrationService.updateRegistrationStatus(1L, 1L, updateRequest);

        assertNotNull(response);
    }

    @Test
    void deleteRegistration_ShouldDelete_WhenExists() {
        when(repository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.of(registration));
        when(eventClient.releaseCapacity(1L, 1L)).thenReturn(ResponseEntity.noContent().build());

        registrationService.deleteRegistration(1L, 1L);

        verify(eventClient).releaseCapacity(1L, 1L);
        verify(repository).delete(registration);
    }

    @Test
    void deleteRegistration_ShouldThrowException_WhenNotFound() {
        when(repository.findByEventIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(RegistrationNotFoundException.class, () -> registrationService.deleteRegistration(1L, 1L));
    }
}
