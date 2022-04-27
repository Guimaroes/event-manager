package com.eventmanager.api.registration.quick;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class QuickRegistrationCreationDTO {
    @NotNull
    private Long eventId;
}
