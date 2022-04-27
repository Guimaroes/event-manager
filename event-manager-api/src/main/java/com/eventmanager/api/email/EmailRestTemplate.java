package com.eventmanager.api.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EmailRestTemplate {

    private static final String EMAIL_URL = "http://localhost:8080/email";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper;

    public void sendEmail(EmailDTO emailDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> body = new HttpEntity<>(objectMapper.writeValueAsString(emailDTO) ,headers);

            restTemplate.postForObject(EMAIL_URL, body, String.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
