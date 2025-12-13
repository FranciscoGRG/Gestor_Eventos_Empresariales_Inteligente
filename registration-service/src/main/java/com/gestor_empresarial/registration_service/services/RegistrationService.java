package com.gestor_empresarial.registration_service.services;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestor_empresarial.registration_service.clients.IEventFeignClient;
import com.gestor_empresarial.registration_service.clients.IUserFeignClient;
import com.gestor_empresarial.registration_service.dtos.EventDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationNotificationDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationRequestDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationResponseDto;
import com.gestor_empresarial.registration_service.dtos.UpdateRegistrationStatusDto;
import com.gestor_empresarial.registration_service.dtos.UserDto;
import com.gestor_empresarial.registration_service.enums.RegistrationStatus;
import com.gestor_empresarial.registration_service.exceptions.AlreadyRegisteredException;
import com.gestor_empresarial.registration_service.exceptions.CapacityErrorException;
import com.gestor_empresarial.registration_service.exceptions.RegistrationNotFoundException;
import com.gestor_empresarial.registration_service.mappers.RegistrationMapper;
import com.gestor_empresarial.registration_service.models.RegistrationModel;
import com.gestor_empresarial.registration_service.repositories.IRegistrationRepository;

@Service
public class RegistrationService {


    private final IRegistrationRepository repository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final IEventFeignClient eventClient;

    private final IUserFeignClient userClient;

    public RegistrationService(IRegistrationRepository repository, KafkaTemplate<String, Object> kafkaTemplate,
            IEventFeignClient eventClient, IUserFeignClient userClient) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.eventClient = eventClient;
        this.userClient = userClient;
    }

    @Transactional
    public RegistrationResponseDto createRegistration(RegistrationRequestDto request, Long userId) {
        if (repository.existsByEventIdAndUserId(request.eventId(), userId)) {
            throw new AlreadyRegisteredException(
                    "El usuario: " + userId + " ya esta inscrito al evento: " + request.eventId());
        }

        try {
            eventClient.reserveCapacityAndRegister(request.eventId(), userId);
        } catch (Exception e) {
            throw new CapacityErrorException("Fallo en la reserva, capacidad del evento llena");
        }

        RegistrationModel registration = new RegistrationModel();
        registration.setEventId(request.eventId());
        registration.setUserId(userId);
        registration.setStatus(RegistrationStatus.PENDING_PAYMENT);

        RegistrationModel savedRegistration = repository.save(registration);

        UserDto user = userClient.getUserNameAndEmail(userId).getBody();
        EventDto event = eventClient.getEventById(userId).getBody();

        RegistrationNotificationDto registrationNotification = new RegistrationNotificationDto(user.userEmail(), user.userName(), event.title(), event.startDate(), event.location());
        kafkaTemplate.send("registration-event", registrationNotification);

        return RegistrationMapper.toResponseDto(savedRegistration);
    }

    public List<RegistrationResponseDto> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(RegistrationMapper::toResponseDto)
                .toList();
    }

    public List<RegistrationResponseDto> findAllByEventId(Long eventId) {
        return repository.findAllByEventId(eventId).stream()
                .map(RegistrationMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public RegistrationResponseDto updateRegistrationStatus(Long eventId, Long userId,
            UpdateRegistrationStatusDto request) {
        RegistrationModel registration = repository.findByEventIdAndUserId(eventId, userId).orElseThrow(
                () -> new RegistrationNotFoundException(
                        "No se ha encontrado el registro con userId: " + userId + " y eventId: " + eventId));

        registration.setStatus(request.newStatus());

        RegistrationModel updatedRegistration = repository.save(registration);

        return RegistrationMapper.toResponseDto(updatedRegistration);
    }

    @Transactional
    public void deleteRegistration(Long userId, Long eventId) {
        RegistrationModel registration = repository.findByEventIdAndUserId(eventId, userId).orElseThrow(
                () -> new RegistrationNotFoundException(
                        "No se ha encontrado el registro con userId: " + userId + " y eventId: " + eventId));

        try {
            eventClient.releaseCapacity(eventId, userId);
        } catch (Exception e) {
            throw new CapacityErrorException("Fallo al reducir la cantidad de registros en el evento");
        }

        repository.delete(registration);
    }
}
