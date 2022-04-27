package com.eventmanager.certificate.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EventRestTemplate {
    private static final String EVENTS_URL = "http://localhost:8080/events/%d";

    private final RestTemplate restTemplate = new RestTemplate();

    public EventDTO getEventById(Long eventId) {
        return restTemplate.getForObject(String.format(EVENTS_URL, eventId), EventDTO.class);
    }
}
