package com.eventmanager.api.event;

import com.eventmanager.api.registration.Registration;
import com.eventmanager.api.registration.RegistrationRepository;
import com.eventmanager.api.registration.RegistrationStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    @GetMapping
    public ResponseEntity<List<Event>> listEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Event>> listUserEventsParticipation(@PathVariable Long id) {
        List<Long> eventIds = registrationRepository.findByUserId(id).stream()
                .filter(registration -> RegistrationStatusEnum.PRESENT.equals(registration.getStatus()))
                .map(Registration::getEventId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventRepository.findAllById(eventIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> addEvent(@Valid @RequestBody EventCreationDTO eventCreationDTO) {
        Event event = Event.builder()
                .title(eventCreationDTO.getTitle())
                .description(eventCreationDTO.getDescription())
                .date(eventCreationDTO.getDate())
                .duration(eventCreationDTO.getDuration())
                .build();

        return new ResponseEntity<>(eventRepository.save(event), HttpStatus.CREATED);
    }
}
