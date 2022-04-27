package com.eventmanager.certificate.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByUuid(String uuid);
    Optional<Certificate> findByRegistrationId(Long registrationId);
    List<Certificate> findByRegistrationIdIn(List<Long> registrationIdList);
}
