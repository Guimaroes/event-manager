package com.eventmanager.api.registration;

import com.eventmanager.api.email.EmailDTO;
import com.eventmanager.api.email.EmailRestTemplate;
import com.eventmanager.api.event.Event;
import com.eventmanager.api.event.EventRepository;
import com.eventmanager.api.user.UserDTO;
import com.eventmanager.api.user.UserRestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.eventmanager.api.registration.RegistrationStatusEnum.*;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRestTemplate userRestTemplate;
    private final EmailRestTemplate emailRestTemplate;

    @GetMapping
    public ResponseEntity<List<Registration>> listRegistrations() {
        return ResponseEntity.ok(registrationRepository.findAll());
    }

    @GetMapping("users/{id}/present")
    public ResponseEntity<List<Registration>> getPresentRegistrationsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(registrationRepository.findByUserId(id).stream()
                .filter(registration -> PRESENT.equals(registration.getStatus()))
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> addRegistration(@Valid @RequestBody RegistrationCreationDTO registrationCreationDTO,
                                             @RequestHeader("Authorization") String token) {
        UserDTO userDTO = userRestTemplate.getUserByIdWithToken(registrationCreationDTO.getUserId(), token);

        Optional<Event> optionalEvent = eventRepository.findById(registrationCreationDTO.getEventId());

        if (optionalEvent.isEmpty()) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        if (registrationRepository.existsByEventIdAndUserId(
                registrationCreationDTO.getEventId(),
                registrationCreationDTO.getUserId())) {
            return ResponseEntity.badRequest().body("User is already registered in this event");
        }

        Registration registration = Registration.builder()
                .eventId(registrationCreationDTO.getEventId())
                .userId(registrationCreationDTO.getUserId())
                .uuid(UUID.randomUUID().toString())
                .status(REGISTERED.getValue())
                .build();

        registration = registrationRepository.save(registration);

        emailRestTemplate.sendEmail(getRegistrationConfirmedEmail(optionalEvent.get().getTitle(), userDTO.getEmail()));

        return new ResponseEntity<>(registration, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable Long id) {
        return registrationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return registrationRepository.findById(id)
                .map(registration -> {
                    UserDTO userDTO = userRestTemplate.getUserByIdWithToken(registration.getUserId(), token);

                    if (CANCELLED.equals(registration.getStatus())) {
                        return new ResponseEntity<>("Registration is already cancelled", HttpStatus.BAD_REQUEST);
                    }

                    if (PRESENT.equals(registration.getStatus())) {
                        return new ResponseEntity<>("You cannot cancel a present status registration", HttpStatus.BAD_REQUEST);
                    }

                    Event event = eventRepository.findById(registration.getEventId()).orElse(null);

                    if (event == null) {
                        return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
                    } else if (ZonedDateTime.now().plusHours(2).isAfter(event.getDate())) {
                        return new ResponseEntity<>("You cannot cancel a registration from an event that will start in less than 2 hours", HttpStatus.BAD_REQUEST);
                    }

                    registration.setStatus(CANCELLED.getValue());

                    registration = registrationRepository.save(registration);

                    emailRestTemplate.sendEmail(getRegistrationCancelledEmail(event.getTitle(), userDTO.getEmail()));

                    return ResponseEntity.ok(registration);
                })
                .orElse(new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}/present")
    public ResponseEntity<?> presentRegistration(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return registrationRepository.findById(id)
                .map(registration -> {
                    UserDTO userDTO = userRestTemplate.getUserByIdWithToken(registration.getUserId(), token);

                    if (CANCELLED.equals(registration.getStatus())) {
                        return new ResponseEntity<>("Registration is already cancelled", HttpStatus.BAD_REQUEST);
                    }

                    if (PRESENT.equals(registration.getStatus())) {
                        return new ResponseEntity<>("Registration is already in present status", HttpStatus.BAD_REQUEST);
                    }

                    Event event = eventRepository.findById(registration.getEventId()).orElse(null);

                    if (event == null) {
                        return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
                    }

                    registration.setStatus(PRESENT.getValue());

                    registration = registrationRepository.save(registration);

                    emailRestTemplate.sendEmail(getRegistrationPresentEmail(event.getTitle(), userDTO.getEmail()));

                    return ResponseEntity.ok(registration);
                })
                .orElse(new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND));
    }

    private EmailDTO getRegistrationConfirmedEmail(String eventTitle, String to) {
        return EmailDTO.builder()
                .subject("Inscrição de evento confirmada")
                .text(String.format("Inscrição no evento %s confirmada!", eventTitle))
                .to(to)
                .build();
    }

    private EmailDTO getRegistrationCancelledEmail(String eventTitle, String to) {
        return EmailDTO.builder()
                .subject("Inscrição de evento cancelada")
                .text(String.format("Inscrição no evento %s cancelada!", eventTitle))
                .to(to)
                .build();
    }

    private EmailDTO getRegistrationPresentEmail(String eventTitle, String to) {
        return EmailDTO.builder()
                .subject("Presença confirmada")
                .text(String.format("Presença no evento %s confirmada!", eventTitle))
                .to(to)
                .build();
    }
}
