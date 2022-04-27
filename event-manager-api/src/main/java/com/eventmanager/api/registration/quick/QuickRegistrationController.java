package com.eventmanager.api.registration.quick;

import com.eventmanager.api.email.EmailDTO;
import com.eventmanager.api.email.EmailRestTemplate;
import com.eventmanager.api.event.Event;
import com.eventmanager.api.event.EventRepository;
import com.eventmanager.api.registration.Registration;
import com.eventmanager.api.registration.RegistrationRepository;
import com.eventmanager.api.user.UserDTO;
import com.eventmanager.api.user.UserRestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static com.eventmanager.api.registration.RegistrationStatusEnum.PRESENT;

@Slf4j
@RestController
@RequestMapping("/registrations/quick")
@RequiredArgsConstructor
public class QuickRegistrationController {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRestTemplate userRestTemplate;
    private final EmailRestTemplate emailRestTemplate;

    @PostMapping
    public ResponseEntity<?> addQuickRegistration(@Valid @RequestBody QuickRegistrationCreationDTO quickRegistrationCreationDTO) {
        if (eventRepository.findById(quickRegistrationCreationDTO.getEventId()).isEmpty()) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        Registration registration = Registration.builder()
                .eventId(quickRegistrationCreationDTO.getEventId())
                .userId(0L)
                .uuid(UUID.randomUUID().toString())
                .status(PRESENT.getValue())
                .build();

        return new ResponseEntity<>(registrationRepository.save(registration), HttpStatus.CREATED);
    }

    @PatchMapping("/redeem")
    public ResponseEntity<?> redeemQuickRegistration(@Valid @RequestBody QuickRegistrationRedeemDTO quickRegistrationRedeemDTO,
                                                     @RequestHeader("Authorization") String token) {
        return registrationRepository.findByUuid(quickRegistrationRedeemDTO.getUuid())
                .map(registration -> {
                    UserDTO userDTO = userRestTemplate.getUserByIdWithToken(quickRegistrationRedeemDTO.getUserId(), token);

                    if (registration.getUserId() != 0L) {
                        return new ResponseEntity<>("Registration is already redeemed", HttpStatus.BAD_REQUEST);
                    }

                    if (registrationRepository.existsByEventIdAndUserId(registration.getEventId(), quickRegistrationRedeemDTO.getUserId())) {
                        return new ResponseEntity<>("User is already registered in this event", HttpStatus.BAD_REQUEST);
                    }

                    Optional<Event> optionalEvent = eventRepository.findById(registration.getEventId());

                    if (optionalEvent.isEmpty()) {
                        return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
                    }

                    registration.setUserId(quickRegistrationRedeemDTO.getUserId());

                    registration = registrationRepository.save(registration);

                    emailRestTemplate.sendEmail(getQuickRegistrationEmail(optionalEvent.get().getTitle(), userDTO.getEmail()));

                    return ResponseEntity.ok(registration);
                })
                .orElse(new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND));
    }

    private EmailDTO getQuickRegistrationEmail(String eventTitle, String to) {
        return EmailDTO.builder()
                .subject("Inscrição de evento resgatada")
                .text(String.format("Inscrição do evento %s resgatada com sucesso!", eventTitle))
                .to(to)
                .build();
    }
}
