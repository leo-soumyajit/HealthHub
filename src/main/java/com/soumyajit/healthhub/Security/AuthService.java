package com.soumyajit.healthhub.Security;

import com.soumyajit.healthhub.DTOS.*;
import com.soumyajit.healthhub.Entities.Enums.Roles;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Exception.ResourceNotFound;
import com.soumyajit.healthhub.Repository.UserRepository;
import com.soumyajit.healthhub.Security.OTPServiceAndValidation.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final OtpService otpService;
    @Autowired
    private JavaMailSender mailSender;


    //signup function with otp validation

    @CachePut(cacheNames = "users",key = "#result.id")
    public UserDetailsDTO signUp(SignUpRequestDTOS signUpRequestDTOS){  // signUp method for user
        Optional<User> user = userRepository.findByEmail(signUpRequestDTOS.getEmail());
        if (user.isPresent()) {
            throw new BadCredentialsException("User with this Email is already present");
        }

        // Check if OTP was verified
        if (!otpService.isOTPVerified(signUpRequestDTOS.getEmail())) {
            throw new BadCredentialsException("OTP not verified");
        }

        User newUser = modelMapper.map(signUpRequestDTOS, User.class);
        newUser.setRoles(Set.of(Roles.USER)); // by default all users are USER
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTOS.getPassword())); // bcrypt the password
        User savedUser = userRepository.save(newUser); // save the user

        // Clear OTP verification status after successful signup
        otpService.clearOTPVerified(signUpRequestDTOS.getEmail());

        sendWelcomeEmail(signUpRequestDTOS);
        return modelMapper.map(savedUser, UserDetailsDTO.class);

    }


    public LoginResponseDTO login(LoginDTOS loginDTOS){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTOS.getEmail(),loginDTOS.getPassword())
        );

        User userEntities = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userEntities);
        String refreshToken = jwtService.generateRefreshToken(userEntities);

        return new LoginResponseDTO(userEntities.getId(),accessToken,refreshToken);

    }

    public String refreshToken(String refreshToken) { //refreshToken method
        Long uerId = jwtService.getUserIdFromToken(refreshToken);  //refresh token is valid
        User user = userRepository.findById(uerId)
                .orElseThrow(()->
                        new ResourceNotFound("User not found with id : "+uerId));
        return jwtService.generateAccessToken(user);
    }

    private void sendWelcomeEmail(SignUpRequestDTOS signUpRequestDTOS) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(signUpRequestDTOS.getEmail());
            helper.setSubject("Welcome to HealthHub!");

            String htmlMsg = "<!DOCTYPE html>"
                    + "<html lang=\"en\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Welcome to HealthHub</title>"
                    + "<style>"
                    + "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                    + ".email-container { max-width: 600px; margin: 40px auto; background: #ffffff; padding: 20px;"
                    + "border-radius: 10px; box-shadow: 0 8px 16px rgba(0,0,0,0.1); }"
                    + ".header { background-color: #00bcd4; color: white; padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }"
                    + ".header h1 { margin: 0; font-size: 26px; }"
                    + ".content { padding: 20px; font-size: 16px; }"
                    + ".footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; }"
                    + ".social-icons { margin-top: 15px; }"
                    + ".social-icons img { width: 24px; margin: 0 8px; vertical-align: middle; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"email-container\">"
                    + "<div class=\"header\"><h1>Welcome, " + signUpRequestDTOS.getName() + "!</h1></div>"
                    + "<div class=\"content\">"
                    + "<p>We're excited to welcome you to <strong>HealthHub</strong>! 🎉</p>"
                    + "<p>Start exploring personalized health features made just for you.</p>"
                    + "<p>Need any help? We’re here to support you anytime!</p>"
                    + "</div>"
                    + "<div class=\"footer\">"
                    + "<hr style=\"border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;\">"
                    + "<div class='social-icons'>"
                    + "<a href='https://www.facebook.com/yourpage'><img src='https://cdn-icons-png.flaticon.com/512/733/733547.png' alt='Facebook'></a>"
                    + "<a href='https://www.instagram.com/yourpage'><img src='https://cdn-icons-png.flaticon.com/512/2111/2111463.png' alt='Instagram'></a>"
                    + "<a href='https://www.linkedin.com/company/yourpage'><img src='https://cdn-icons-png.flaticon.com/512/145/145807.png' alt='LinkedIn'></a>"
                    + "</div><br>"
                    + "HealthHub | Haldia, India | Contact Support<br>"
                    + "<em>If you didn’t sign up, please ignore this email.</em>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
            System.out.println("✅ Welcome email sent successfully to " + signUpRequestDTOS.getEmail());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
