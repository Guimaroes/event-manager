package com.eventmanager.email.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.name}")
    private String name;

    public void sendSimpleMessage(EmailDTO emailDTO) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            message.setFrom(new InternetAddress(from, name));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailDTO.getTo()));
            message.setSubject(emailDTO.getSubject());
            message.setText(emailDTO.getText());

            emailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
