package com.gestor_empresarial.registration_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestor_empresarial.registration_service.models.RegistrationModel;

@Repository
public interface IRegistrationRepository extends JpaRepository<RegistrationModel, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Optional<RegistrationModel> findByEventIdAndUserId(Long eventId, Long userId);
    List<RegistrationModel> findAllByEventId(Long eventId);
    List<RegistrationModel> findAllByUserId(Long userId);
}
