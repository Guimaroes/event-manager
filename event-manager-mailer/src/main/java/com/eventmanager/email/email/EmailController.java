package com.eventmanager.email.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public void sendEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendSimpleMessage(emailDTO);
    }
}
