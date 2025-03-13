package com.soumyajit.healthhub.EmailService;

import com.soumyajit.healthhub.Advices.ApiError;
import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Service.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> requestPasswordReset(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        ApiResponse<String> response = new ApiResponse<>();
        if (user.isPresent()) {
            String token = tokenService.createResetCode(email);
            emailService.sendPasswordResetEmail(email, token);
            response.setData("Password reset email sent");
            return ResponseEntity.ok(response);
        } else {
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "User not found");
            response.setApiError(apiError);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Optional<String> email = tokenService.getEmailByCode(token);
        ApiResponse<String> response = new ApiResponse<>();
        if (email.isPresent()) {
            Optional<User> user = userService.findByEmail(email.get());
            if (user.isPresent()) {
                userService.updateUserPassword(user.get(), newPassword); // Password encoded in service
                tokenService.invalidateCode(token);
                response.setData("Password reset successful");

                //send email that your password is Successfully reset
//                SimpleMailMessage message = new SimpleMailMessage();
//                message.setTo(email.get());
//                message.setSubject("Successfully reset password");
//                message.setText("Your password is successfully reset now you can login with your new password ");
//                mailSender.send(message);
                sendPasswordResetSuccessEmail(email);

                return ResponseEntity.ok(response);

            } else {
                ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "User not found");
                response.setApiError(apiError);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid token");
            response.setApiError(apiError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    public void sendPasswordResetSuccessEmail(Optional<String> emailOpt) {
        if (emailOpt.isPresent()) {
            String email = emailOpt.get();
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(email);
                helper.setSubject("Password Reset Successful");

                String htmlMsg = "<!DOCTYPE html>"
                        + "<html lang=\"en\">"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<title>Password Reset Successful</title>"
                        + "<style>"
                        + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; color: #333; }"
                        + ".email-container { width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                        + ".header { background-color: #00bcd4; color: #ffffff; padding: 10px 0; text-align: center; border-radius: 10px 10px 0 0; }"
                        + ".header h1 { margin: 0; font-size: 24px; }"
                        + ".content { padding: 20px; }"
                        + ".content p { margin: 10px 0; }"
                        + ".footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; }"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "<div class=\"email-container\">"
                        + "<div class=\"header\"><h1>Password Reset Successful</h1></div>"
                        + "<div class=\"content\">"
                        + "<p>Hello,</p>"
                        + "<p>Your password has been successfully reset. You can now log in with your new password.</p>"
                        + "<p>If you did not request a password reset, please contact our support team immediately.</p>"
                        + "<p>Thank you!</p>"
                        + "</div>"
                        + "<div class=\"footer\"><p>HealthHub | Haldia , India | Contact Support</p><p>If you didn’t request this, please ignore this email.</p></div>"
                        + "</div>"
                        + "</body>"
                        + "</html>";

                helper.setText(htmlMsg, true);  // Set to true to indicate that the message is HTML

                mailSender.send(message);
                System.out.println("Password reset success email sent successfully to " + email);
            } catch (MessagingException e) {
                System.err.println("Failed to send password reset success email: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Email address is missing. Cannot send password reset success email.");
        }
    }





}