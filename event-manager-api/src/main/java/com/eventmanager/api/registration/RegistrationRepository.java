package com.eventmanager.api.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<Registration> findByUuid(String uuid);

    List<Registration> findByUserId(Long userId);
}