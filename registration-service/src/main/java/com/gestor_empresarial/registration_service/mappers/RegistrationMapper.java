package com.gestor_empresarial.registration_service.mappers;

import com.gestor_empresarial.registration_service.dtos.RegistrationResponseDto;
import com.gestor_empresarial.registration_service.models.RegistrationModel;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RegistrationMapper {

    public static RegistrationResponseDto toResponseDto(RegistrationModel model) {
        if (model == null) {
            return null;
        }

        return new RegistrationResponseDto(
                model.getId(),
                model.getUserId(),
                model.getEventId(),
                model.getStatus());
    }
}
