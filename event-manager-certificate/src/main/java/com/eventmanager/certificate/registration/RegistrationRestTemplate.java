package com.eventmanager.certificate.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationRestTemplate {
    private static final String REGISTRATIONS_URL = "http://localhost:8080/registrations/%d";
    private static final String USER_PRESENT_REGISTRATIONS_URL = "http://localhost:8080/registrations/users/%d/present";

    private final RestTemplate restTemplate = new RestTemplate();

    public RegistrationDTO getRegistrationById(Long registrationId) {
        return restTemplate.getForObject(String.format(REGISTRATIONS_URL, registrationId), RegistrationDTO.class);
    }

    public List<RegistrationDTO> getPresentRegistrationsByUserId(Long userId) {
        RegistrationDTO[] registrationDTOS = restTemplate.getForObject(String.format(USER_PRESENT_REGISTRATIONS_URL, userId), RegistrationDTO[].class);
        return registrationDTOS == null ? List.of() : List.of(registrationDTOS);
    }
}
