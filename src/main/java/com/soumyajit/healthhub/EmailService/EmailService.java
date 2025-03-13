package com.soumyajit.healthhub.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;


    public void sendPasswordResetEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Password Reset Request");

            String htmlMsg = "<!DOCTYPE html>"
                    + "<html lang=\"en\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Password Reset Request</title>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; color: #333; }"
                    + ".email-container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                    + ".header { background-color: #00bcd4; color: #ffffff; padding: 10px 0; text-align: center; border-radius: 10px 10px 0 0; }"
                    + ".header h1 { margin: 0; font-size: 24px; }"
                    + ".content { padding: 20px; }"
                    + ".content p { margin: 10px 0; }"
                    + ".token { font-size: 24px; font-weight: bold; color: #00bcd4; text-align: center; margin: 20px 0; }"
                    + ".footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"email-container\">"
                    + "<div class=\"header\"><h1>Password Reset Request</h1></div>"
                    + "<div class=\"content\">"
                    + "<p>Hello,</p>"
                    + "<p>We received a request to reset your password for your account associated with this email address. Please use the following token to reset your password:</p>"
                    + "<div class=\"token\">" + token + "</div>"
                    + "<p>This token is valid for only 10 minutes. If you did not request a password reset, please ignore this email or contact our support team for assistance.</p>"
                    + "<p>Thank you!</p>"
                    + "</div>"
                    + "<div class=\"footer\"><p>Noctrune IDE | Haldia , India | Contact Support</p><p>If you didn’t request this, please ignore this email.</p></div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlMsg, true);  // Set to true to indicate that the message is HTML

            mailSender.send(message);
            System.out.println("Password reset email sent successfully to " + email);
        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }
}
