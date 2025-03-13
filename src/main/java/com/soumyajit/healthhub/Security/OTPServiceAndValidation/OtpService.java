package com.soumyajit.healthhub.Security.OTPServiceAndValidation;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;


@Service
public class OtpService {

    @Autowired
    private JavaMailSender emailSender;

    private final Map<String, OTPDetails> otpStorage = new HashMap<>();
    private final Map<String, Boolean> otpVerified = new HashMap<>();

    public String generateOTP() {
        int otp = (int)(Math.random() * 9000) + 1000;
        return String.valueOf(otp);
    }

    public void sendOTPEmail(String email, String otp) {
        try {
            // Validate email address format
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("idenocturne@gmail.com");
            helper.setTo(email);
            helper.setSubject("Your OTP Code");

            String htmlMsg = "<!DOCTYPE html>"
                    + "<html lang=\"en\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Email Verification</title>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; color: #333; }"
                    + ".email-container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                    + ".header { background-color: #00bcd4; color: #ffffff; padding: 10px 0; text-align: center; border-radius: 10px 10px 0 0; }"
                    + ".header h1 { margin: 0; font-size: 24px; }"
                    + ".content { padding: 20px; }"
                    + ".content p { margin: 10px 0; }"
                    + ".verification-code { font-size: 28px; font-weight: bold; color: #00bcd4; text-align: center; margin: 20px 0; }"
                    + ".footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"email-container\">"
                    + "<div class=\"header\"><h1>Hello, Coder!</h1></div>"
                    + "<div class=\"content\">"
                    + "<p>Your one time password for <strong>Nocturne IDE</strong> is:</p>"
                    + "<div class=\"verification-code\">" + otp + "</div>"
                    + "<p>Please enter this code on the website to verify your email.</p>"
                    + "<p>Thank you!</p>"
                    + "</div>"
                    + "<div class=\"footer\"><p>Nocturne IDE | Haldia , India | Contact Support</p><p>If you didn’t request this, please ignore this email.</p></div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlMsg, true);  // Set to true to indicate that the message is HTML

            emailSender.send(message);
            System.out.println("OTP sent successfully. Check your gmail.");
        } catch (MessagingException e) {
            System.err.println("Failed to send OTP: " + e.getMessage());
            e.printStackTrace();
        } catch (jakarta.mail.MessagingException e) {
            System.err.println("Invalid email address: " + e.getMessage());
        }
    }


    public void saveOTP(String email, String otp) {
        otpStorage.put(email, new OTPDetails(otp, System.currentTimeMillis()));
        otpVerified.put(email, false); // Set OTP verification status to false
    }

    public OTPDetails getOTPDetails(String email) {
        return otpStorage.get(email);
    }

    public void deleteOTP(String email) {
        otpStorage.remove(email);
        // Do not remove verification status here to retain it for signup
    }

    public void clearOTPVerified(String email) {
        otpVerified.remove(email); // Clear OTP verification status
    }

    public boolean isOTPExpired(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - timestamp) > (10 * 60 * 1000); // 10 minutes in milliseconds
    }

    public void markOTPVerified(String email) {
        otpVerified.put(email, true); // Mark OTP as verified
    }

    public boolean isOTPVerified(String email) {
        return otpVerified.getOrDefault(email, false);
    }
}
