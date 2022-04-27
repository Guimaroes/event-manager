package com.eventmanager.certificate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserRestTemplate {

    private static final String USER_URL = "http://localhost:8080/users/%d";

    private final RestTemplate restTemplate = new RestTemplate();

    public UserDTO getUserByIdWithToken(Long userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<UserDTO> response = restTemplate.exchange(String.format(USER_URL, userId), HttpMethod.GET, request, UserDTO.class);

        return response.getBody();
    }
}
