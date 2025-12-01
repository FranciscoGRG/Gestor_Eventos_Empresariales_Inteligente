package com.gestor_empresarial.registration_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestor_empresarial.registration_service.dtos.RegistrationRequestDto;
import com.gestor_empresarial.registration_service.dtos.RegistrationResponseDto;
import com.gestor_empresarial.registration_service.dtos.UpdateRegistrationStatusDto;
import com.gestor_empresarial.registration_service.services.RegistrationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService service;

    @GetMapping("/user")
    public ResponseEntity<List<RegistrationResponseDto>> getAllRegistrationsByUserId(
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(service.findAllByUserId(userId));
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<List<RegistrationResponseDto>> findAllByEventID(@PathVariable Long id) {
        return ResponseEntity.ok(service.findAllByEventId(id));
    }

    @PostMapping()
    public ResponseEntity<RegistrationResponseDto> createRegistration(@RequestBody RegistrationRequestDto request,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(service.createRegistration(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistrationResponseDto> updateRegistrationStatus(@PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId, @RequestBody UpdateRegistrationStatusDto request) {
        return ResponseEntity.ok(service.updateRegistrationStatus(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@RequestHeader("X-User-ID") Long userId, @PathVariable Long id) {
        service.deleteRegistration(userId, id);
        return ResponseEntity.noContent().build();
    }

}
