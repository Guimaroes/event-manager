package com.eventmanager.certificate.registration;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    private Long eventId;
    private Long userId;
    private String status;
}
