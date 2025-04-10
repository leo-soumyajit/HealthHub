package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.DTOS.DoctorApplicationRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorApplicationController {

    private final JavaMailSender mailSender;

    @Value("${admin.review.email}")
    private String adminEmail;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<String>> applyForDoctorRole(@ModelAttribute DoctorApplicationRequest request) {
        try {
            // ====================
            // ADMIN EMAIL
            // ====================
            MimeMessage adminMessage = mailSender.createMimeMessage();
            MimeMessageHelper adminHelper = new MimeMessageHelper(adminMessage, true);

            adminHelper.setTo(adminEmail);
            adminHelper.setSubject("🩺 New Doctor Role Application - " + request.getName());

            String htmlContent = """
                <html>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f8fc;">
                    <div style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <h2 style="color: #003366;">🩺 New Doctor Application Received</h2>
                            <p style="color: #888;">A doctor has submitted an application to join HealthHub.</p>
                        </div>
                        <p style="font-size: 16px; color: #444;">Here’s the applicant’s information:</p>

                        <div style="background-color: #f9f9f9; padding: 15px; border-left: 4px solid #003366; margin: 20px 0;">
                            <p><strong>Name:</strong> %s</p>
                            <p><strong>Email:</strong> %s</p>
                            <p><strong>Education:</strong> %s</p>
                            <p><strong>Experience:</strong> %s</p>
                            <p><strong>Doctor Registration Number:</strong> %s</p>
                            <p>📎 Attachments: License Document & Hospital ID</p>
                        </div>

                        <div style="margin-top: 30px; text-align: center; font-size: 14px; color: #666;">
                            <hr style="margin: 30px 0; border: none; border-top: 1px solid #ddd;" />
                            <p>HealthHub Admin Portal</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    request.getName(),
                    request.getEmail(),
                    request.getEducation(),
                    request.getExperiences(),
                    request.getRegistrationNumber()
            );

            adminHelper.setText(htmlContent, true);

            // ✅ FIX: preserve original filenames
            if (request.getLicenseDocument() != null && !request.getLicenseDocument().isEmpty()) {
                adminHelper.addAttachment(request.getLicenseDocument().getOriginalFilename(), request.getLicenseDocument());
            }

            if (request.getHospitalIdImage() != null && !request.getHospitalIdImage().isEmpty()) {
                adminHelper.addAttachment(request.getHospitalIdImage().getOriginalFilename(), request.getHospitalIdImage());
            }

            mailSender.send(adminMessage);

            // ====================
            // APPLICANT COPY
            // ====================
            MimeMessage applicantMessage = mailSender.createMimeMessage();
            MimeMessageHelper applicantHelper = new MimeMessageHelper(applicantMessage, true);

            applicantHelper.setTo(request.getEmail());
            applicantHelper.setSubject("📨 Doctor Application Received - HealthHub");

            String applicantCopy = """
                <html>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f8fc;">
                    <div style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <h2 style="color: #003366;">✅ Application Received</h2>
                            <p style="color: #888;">Thank you <strong>%s</strong> for applying to be a HealthHub doctor! We’ve successfully received your doctor application. Our team will review it and contact you soon.</p>
                        </div>
                        <p style="font-size: 16px; color: #444;">Here’s a copy of the information you submitted:</p>

                        <div style="background-color: #f9f9f9; padding: 15px; border-left: 4px solid #003366; margin: 20px 0;">
                            <p><strong>Name:</strong> %s</p>
                            <p><strong>Email:</strong> %s</p>
                            <p><strong>Education:</strong> %s</p>
                            <p><strong>Experience:</strong> %s</p>
                            <p><strong>Doctor Registration Number:</strong> %s</p>
                        </div>

                        <div style="margin-top: 30px; text-align: center; font-size: 14px; color: #666;">
                            <hr style="margin: 30px 0; border: none; border-top: 1px solid #ddd;" />
                            <p>HealthHub | Haldia, India | Contact Support</p>
                            <div style="margin-bottom: 10px;">
                                <a href="https://www.instagram.com" style="margin: 0 8px;">
                                    <img src="https://cdn-icons-png.flaticon.com/512/1384/1384063.png" width="18" alt="Instagram" />
                                </a>
                                <a href="https://www.facebook.com" style="margin: 0 8px;">
                                    <img src="https://cdn-icons-png.flaticon.com/512/1384/1384053.png" width="18" alt="Facebook" />
                                </a>
                                <a href="https://www.linkedin.com" style="margin: 0 8px;">
                                    <img src="https://cdn-icons-png.flaticon.com/512/145/145807.png" width="18" alt="LinkedIn" />
                                </a>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
            """.formatted(
                    request.getName(),
                    request.getName(),
                    request.getEmail(),
                    request.getEducation(),
                    request.getExperiences(),
                    request.getRegistrationNumber()
            );

            applicantHelper.setText(applicantCopy, true);
            mailSender.send(applicantMessage);

            return ResponseEntity.ok(new ApiResponse<>("Application submitted successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>("Failed to submit application."));
        }
    }
}
