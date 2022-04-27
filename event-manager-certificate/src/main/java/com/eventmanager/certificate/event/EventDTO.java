package com.eventmanager.certificate.event;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String title;
    private ZonedDateTime date;
}
