// File: src/main/java/com/healthhub/service/MailService.java
package com.soumyajit.healthhub.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    // Now the recipient's email is passed in as a parameter
    public void sendAlertEmail(String toEmail, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
       // message.setText(body);
        mailSender.send(message);
    }
}

