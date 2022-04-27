package com.eventmanager.api.email;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String to;
    private String subject;
    private String text;
}
