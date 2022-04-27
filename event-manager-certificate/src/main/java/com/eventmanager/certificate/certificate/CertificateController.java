package com.eventmanager.certificate.certificate;

import com.eventmanager.certificate.event.EventDTO;
import com.eventmanager.certificate.event.EventRestTemplate;
import com.eventmanager.certificate.registration.RegistrationDTO;
import com.eventmanager.certificate.registration.RegistrationRestTemplate;
import com.eventmanager.certificate.user.UserDTO;
import com.eventmanager.certificate.user.UserRestTemplate;
import com.eventmanager.certificate.certificate.validation.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateRepository certificateRepository;

    private final UserRestTemplate userRestTemplate;
    private final EventRestTemplate eventRestTemplate;
    private final RegistrationRestTemplate registrationRestTemplate;

    @GetMapping("/{uuid}")
    public ResponseEntity<MessageDTO> validateCertificate(@PathVariable String uuid,
                                                 @RequestHeader("Authorization") String token) {
        return certificateRepository.findByUuid(uuid)
                .map(certificate -> {
                    RegistrationDTO registrationDTO = registrationRestTemplate.getRegistrationById(certificate.getRegistrationId());
                    EventDTO eventDTO = eventRestTemplate.getEventById(registrationDTO.getEventId());
                    UserDTO userDTO = userRestTemplate.getUserByIdWithToken(registrationDTO.getUserId(), token);

                    MessageDTO messageDTO = MessageDTO.builder()
                            .message(String.format("Este certificado reconhece que %s esteve presente no evento %s na data %s",
                                    userDTO.getUsername(),
                                    eventDTO.getTitle(),
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy").format(eventDTO.getDate())))
                            .build();

                    return ResponseEntity.ok(messageDTO);
                })
                .orElse(new ResponseEntity<>(MessageDTO.builder().message("Certificado inválido").build(), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Certificate>> listUserCertificates(@PathVariable Long id) {
        List<Long> registrationIds = registrationRestTemplate.getPresentRegistrationsByUserId(id).stream().map(RegistrationDTO::getId).collect(Collectors.toList());
        return ResponseEntity.ok(certificateRepository.findByRegistrationIdIn(registrationIds));
    }

    @PostMapping
    public ResponseEntity<?> createCertificate(@Valid @RequestBody CertificateCreationDTO certificateCreationDTO) {
        Certificate certificate = certificateRepository.findByRegistrationId(certificateCreationDTO.getRegistrationId()).orElse(null);

        if (certificate != null) {
            return new ResponseEntity<>(MessageDTO.builder().message("Um certificado já foi gerado para essa inscrição").build(), HttpStatus.BAD_REQUEST);
        }

        RegistrationDTO registrationDTO = registrationRestTemplate.getRegistrationById(certificateCreationDTO.getRegistrationId());

        if (!registrationDTO.getStatus().equals("present")) {
            return new ResponseEntity<>(
                    MessageDTO.builder().message("O status da inscrição deve ser 'Presente' para gerar um certificado").build(),
                    HttpStatus.BAD_REQUEST);
        }

        certificate = Certificate.builder()
                .registrationId(certificateCreationDTO.getRegistrationId())
                .uuid(UUID.randomUUID().toString())
                .build();

        return ResponseEntity.ok(certificateRepository.save(certificate));
    }
}
