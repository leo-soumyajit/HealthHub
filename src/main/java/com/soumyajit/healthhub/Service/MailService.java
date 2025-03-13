package com.soumyajit.healthhub.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    public void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);  // Enable HTML content
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendAlertEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Enable HTML content

            mailSender.send(message);
            log.info("✅ Email sent successfully to {}", to);

        } catch (MessagingException e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
