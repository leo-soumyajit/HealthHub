package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.Entities.Enums.Roles;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Exception.ResourceNotFound;
import com.soumyajit.healthhub.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class adminServiceImpl implements adminService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Override
    public void makeUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User with this id not found"));

        user.getRoles().add(Roles.ADMIN);
        log.info("Making user Admin with this ID: {}", userId);
        userRepository.save(user);
    }

    @Override
    public void makeUserDoctor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User with this id not found"));

        user.getRoles().add(Roles.DOCTOR);
        log.info("Making user Doctor with this ID: {}", userId);
        userRepository.save(user);

        // Send Email Notification
        try {
            sendDoctorPromotionEmail(user);
        } catch (MessagingException e) {
            log.error("Failed to send promotion email to user with ID: {}", userId, e);
        }
    }

    private void sendDoctorPromotionEmail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setTo(user.getEmail());
        helper.setSubject("🎉 You're Now a Doctor on HealthGuru!");
        helper.setText(buildDoctorPromotionEmail(user.getName()), true);

        mailSender.send(message);
        log.info("Doctor promotion email sent to {}", user.getEmail());
    }

    private String buildDoctorPromotionEmail(String name) {
        return """
        <div style="font-family: 'Segoe UI', sans-serif; background-color: #f4f8fc; padding: 40px 20px;">
            <table width="100%%" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden;">
                <tr>
                    <td style="padding: 30px; text-align: center;">
                        <h2 style="margin: 0; color: #0066cc; font-size: 26px;">🎉 Congratulations, Doctor!</h2>
                    </td>
                </tr>
                <tr>
                    <td style="padding: 0 30px 20px 30px; color: #333;">
                        <p style="font-size: 16px; margin: 0;">Hi <strong>%s</strong>,</p>
                        <p style="font-size: 16px; margin-top: 10px; line-height: 1.6;">
                            We're excited to let you know your account has been <strong>promoted to Doctor</strong> status on <strong>HealthGuru</strong>!
                            You can now write and share your health expertise with our growing community of users.
                        </p>
                    </td>
                </tr>
                <tr>
                    <td style="padding: 20px 30px; text-align: center;">
                        <a href="https://your-frontend-url.com/create-article" 
                           style="background-color: #0066cc; color: white; text-decoration: none; padding: 12px 28px; font-weight: bold; border-radius: 6px; font-size: 16px; display: inline-block;">
                            ✍️ Start Writing Articles
                        </a>
                    </td>
                </tr>
                <tr>
                    <td style="padding: 0 30px 30px 30px; color: #555;">
                        <p style="font-size: 15px; margin: 0; line-height: 1.6;">
                            Thank you for being a valued contributor to <strong>HealthGuru</strong>.
                        </p>
                        <p style="font-size: 14px; color: #777; margin-top: 20px;">— The HealthGuru Team</p>
                    </td>
                </tr>
            </table>
        </div>
    """.formatted(name);
    }

}
