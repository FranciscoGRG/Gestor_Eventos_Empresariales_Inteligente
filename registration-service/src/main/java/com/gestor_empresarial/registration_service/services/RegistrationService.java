package com.gestor_empresarial.registration_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestor_empresarial.registration_service.clients.IEventFeignClient;
import com.gestor_empresarial.registration_service.dtos.RegistrationRequestDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationResponseDto;
import com.gestor_empresarial.registration_service.dtos.UpdateRegistrationStatusDto;
import com.gestor_empresarial.registration_service.enums.RegistrationStatus;
import com.gestor_empresarial.registration_service.exceptions.AlreadyRegisteredException;
import com.gestor_empresarial.registration_service.exceptions.RegistrationNotFoundException;
import com.gestor_empresarial.registration_service.mappers.RegistrationMapper;
import com.gestor_empresarial.registration_service.models.RegistrationModel;
import com.gestor_empresarial.registration_service.repositories.IRegistrationRepository;



@Service
public class RegistrationService {

    @Autowired
    private IRegistrationRepository repository;

    @Autowired
    private IEventFeignClient eventClient;

    @Transactional
    public RegistrationResponseDto createRegistration(RegistrationRequestDto request, Long userId) {
        if (repository.existsByEventIdAndUserId(request.eventId(), userId)) {
            throw new AlreadyRegisteredException(
                    "El usuario: " + userId + " ya esta inscrito al evento: " + request.eventId());
        }

        try {
            eventClient.reserveCapacityAndRegister(request.eventId());
        } catch (Exception e) {
            throw new RuntimeException("Fallo en la reserva de capacidad del event service: " + e.getMessage(), e);
        }

        RegistrationModel registration = new RegistrationModel();
        registration.setEventId(request.eventId());
        registration.setUserId(userId);
        registration.setStatus(RegistrationStatus.PENDING_PAYMENT);

        RegistrationModel savedRegistration = repository.save(registration);

        return RegistrationMapper.toResponseDto(savedRegistration);
    }

    public List<RegistrationResponseDto> findAllByUserId(Long userId) {
        List<RegistrationModel> registrations = repository.findAllByUserId(userId);

        List<RegistrationResponseDto> registrationsDto = registrations.stream()
                .map(RegistrationMapper::toResponseDto)
                .collect(Collectors.toList());

        return registrationsDto;
    }

    public List<RegistrationResponseDto> findAllByEventId(Long eventId) {
        List<RegistrationModel> registrations = repository.findAllByEventId(eventId);

        List<RegistrationResponseDto> registrationsDto = registrations.stream()
                .map(RegistrationMapper::toResponseDto)
                .collect(Collectors.toList());

        return registrationsDto;
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
            eventClient.releaseCapacity(eventId);
        } catch (Exception e) {
            throw new RuntimeException("Fallo al reducir la cantidad de registros en event service: " + e.getMessage(),
                    e);
        }

        repository.delete(registration);
    }
}
