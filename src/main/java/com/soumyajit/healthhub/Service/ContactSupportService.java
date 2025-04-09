package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.ContactSupportDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactSupportService {

    private final JavaMailSender mailSender;

    public void sendSupportMessage(ContactSupportDTO contactDTO) throws Exception {
        // Send email to admin/support
        MimeMessage messageToSupport = mailSender.createMimeMessage();
        MimeMessageHelper helperToSupport = new MimeMessageHelper(messageToSupport, true, "UTF-8");

        helperToSupport.setTo("healthhubteam2025@gmail.com"); // Admin email
        helperToSupport.setSubject("📩 Support Request: " + contactDTO.getSubject());

        String htmlContentToSupport = """
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f8fc;">
                <div style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <h2 style="color: #003366; font-size: 24px;">📞 New Support Request</h2>
                        <p style="color: #888;">A user has submitted a support message.</p>
                    </div>

                    <table style="width: 100%%; font-size: 16px; color: #333; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 10px; font-weight: bold; width: 120px;">👤 Name:</td>
                            <td style="padding: 10px;">%s</td>
                        </tr>
                        <tr style="background-color: #f9f9f9;">
                            <td style="padding: 10px; font-weight: bold;">📧 Email:</td>
                            <td style="padding: 10px;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; font-weight: bold;">📝 Subject:</td>
                            <td style="padding: 10px;">%s</td>
                        </tr>
                        <tr style="background-color: #f9f9f9;">
                            <td style="padding: 10px; font-weight: bold; vertical-align: top;">💬 Message:</td>
                            <td style="padding: 10px;">%s</td>
                        </tr>
                    </table>

                    <div style="margin-top: 30px; text-align: center; font-size: 14px; color: #666;">
                        <hr style="margin: 30px 0; border: none; border-top: 1px solid #ddd;" />
                        <p>HealthHub | Haldia , India | Contact Support</p>
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
                contactDTO.getName(),
                contactDTO.getEmail(),
                contactDTO.getSubject(),
                contactDTO.getMessage().replace("\n", "<br/>")
        );

        helperToSupport.setText(contactDTO.getMessage(), htmlContentToSupport);
        mailSender.send(messageToSupport);

        // Auto-reply to user
        MimeMessage autoReply = mailSender.createMimeMessage();
        MimeMessageHelper helperToUser = new MimeMessageHelper(autoReply, true, "UTF-8");

        helperToUser.setTo(contactDTO.getEmail());
        helperToUser.setSubject("✅ We've received your message");

        String htmlContentToUser = """
            <html>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f8fc;">
                <div style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px;">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <h2 style="color: #003366; font-size: 24px;">📨 Thank you for contacting us!</h2>
                        <p style="color: #888;">We’ve received your message and our team will reach out soon.</p>
                    </div>

                    <p style="font-size: 16px; color: #444;">Hi <strong>%s</strong>,</p>
                    <p style="font-size: 16px; color: #444;">
                        Thanks for reaching out to us. We’ve successfully received your support request and will get back to you as soon as possible.
                    </p>
                    <p style="font-size: 16px; color: #444;">
                        Here's a copy of your message:
                    </p>

                    <div style="background-color: #f9f9f9; padding: 15px; border-left: 4px solid #003366; margin: 20px 0;">
                        <p style="margin: 0;"><strong>Subject:</strong> %s</p>
                        <p style="margin: 5px 0;"><strong>Message:</strong><br/>%s</p>
                    </div>

                    <div style="margin-top: 30px; text-align: center; font-size: 14px; color: #666;">
                        <hr style="margin: 30px 0; border: none; border-top: 1px solid #ddd;" />
                        <p>HealthHub | Haldia , India | Contact Support</p>
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
                contactDTO.getName(),
                contactDTO.getSubject(),
                contactDTO.getMessage().replace("\n", "<br/>")
        );

        helperToUser.setText("We have received your message and will get back to you soon.", htmlContentToUser);
        mailSender.send(autoReply);
    }
}
