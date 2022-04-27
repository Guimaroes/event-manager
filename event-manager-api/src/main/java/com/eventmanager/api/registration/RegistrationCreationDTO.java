package com.eventmanager.api.registration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationCreationDTO {
    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;
}
