package com.gestor_empresarial.event_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestor_empresarial.event_service.models.Event;

@Repository
public interface IEventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndOrganizerId(Long eventId, Long organizerId);
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String title);
}
