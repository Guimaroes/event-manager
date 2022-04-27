package com.eventmanager.certificate.certificate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CertificateCreationDTO {
    @NotNull
    private Long registrationId;
}
