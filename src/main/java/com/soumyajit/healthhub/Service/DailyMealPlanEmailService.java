package com.soumyajit.healthhub.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soumyajit.healthhub.Entities.UserMealPlan;
import com.soumyajit.healthhub.Repository.UserMealPlanRepository;
import com.soumyajit.healthhub.Entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMealPlanEmailService {

    private final UserMealPlanRepository userMealPlanRepository;
    private final UserService userService;
    private final MailService mailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Runs daily at 7 AM
    @Scheduled(cron = "0 44 3 * * ?") // Runs at 02:06 AM Server Time
    public void sendDailyMealPlanEmails() {
        log.info("🔄 Executing Meal Plan Email Scheduler at {}", LocalDateTime.now());

        // Fetch all users
        List<User> users = userService.getAllUsers();
        log.info("📌 Total users fetched: {}", users.size());

        // Use system's default timezone OR a specific one (e.g., Asia/Kolkata)
        ZoneId zone = ZoneId.systemDefault(); // or ZoneId.of("Asia/Kolkata")
        String today = LocalDate.now(zone)
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        log.info("🌍 Server Timezone: {}", zone);
        log.info("📅 Today is: {}", today);

        for (User user : users) {
            log.info("➡️ Processing meal plan for user: {}", user.getEmail());

            Optional<UserMealPlan> activePlanOpt = userMealPlanRepository.findByUserIdAndActiveTrue(user.getId());
            if (activePlanOpt.isEmpty()) {
                log.warn("⚠️ No active meal plan found for user {}", user.getEmail());
                continue;
            }

            UserMealPlan activePlan = activePlanOpt.get();

            try {
                // Parse the composite JSON stored in the mealPlanContent field.
                Map<String, Object> composite = objectMapper.readValue(
                        activePlan.getMealPlanContent(), new TypeReference<Map<String, Object>>() {}
                );

                // Extract the meal plan part (ignore the healthGoal) and convert it to the expected type.
                Map<String, Map<String, List<String>>> structuredMealPlan = objectMapper.convertValue(
                        composite.get("mealPlan"), new TypeReference<Map<String, Map<String, List<String>>>>() {}
                );

                log.info("✅ Successfully parsed meal plan for {}", user.getEmail());
                log.info("📋 Available keys in meal plan: {}", structuredMealPlan.keySet());

                // Get today's meal plan
                Map<String, List<String>> todaysPlan = structuredMealPlan.get(today);
                if (todaysPlan == null || todaysPlan.isEmpty()) {
                    log.warn("⚠️ No meal plan found for {} on {}!", user.getEmail(), today);
                    continue;
                }

                // Compose email content (this method remains unchanged)
                String emailContent = composeEmailContent(todaysPlan);

                try {
                    mailService.sendAlertEmail(user.getEmail(), "Today's Meal Plan", emailContent);
                    log.info("📩 Meal plan email sent successfully to {}", user.getEmail());
                } catch (Exception e) {
                    log.error("❌ Failed to send email to {}: {}", user.getEmail(), e.getMessage(), e);
                }

            } catch (Exception e) {
                log.error("❌ Error processing meal plan for {}: {}", user.getEmail(), e.getMessage(), e);
            }
        }
    }



    /**
     * Generates a well-styled HTML email for the meal plan.
     */
    private String composeEmailContent(Map<String, List<String>> todaysPlan) {
        StringBuilder builder = new StringBuilder();

        builder.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Today's Meal Plan</title>")
                .append("<style>")
                .append("body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; ")
                .append("background-image: url('https://res.cloudinary.com/dho5umnhe/image/upload/v1744226503/food-removebg-preview_eflja9.png'); background-size: cover; background-repeat: no-repeat; background-position: center; }")
                .append(".container { max-width: 600px; margin: 40px auto; background: rgba(255, 255, 255, 0.95); padding: 20px; ")
                .append("border-radius: 10px; box-shadow: 0 8px 16px rgba(0,0,0,0.15); }")
                .append(".header { background: #007bff; color: #fff; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }")
                .append(".header h1 { margin: 0; font-size: 26px; }")
                .append(".meal-section { padding: 15px; border-bottom: 1px solid #ddd; }")
                .append(".meal-section:last-child { border-bottom: none; }")
                .append(".meal-section h2 { color: #007bff; font-size: 20px; margin-bottom: 10px; }")
                .append("ul { list-style-type: none; padding: 0; }")
                .append("li { background: #f8f9fa; padding: 10px; margin-bottom: 5px; border-radius: 5px; }")
                .append(".footer { text-align: center; padding: 20px; font-size: 14px; color: #666; }")
                .append(".social-icons img { width: 24px; margin: 0 8px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<div class='header'><h1>🍽️ Your Meal Plan for Today</h1></div>");

        todaysPlan.forEach((mealType, details) -> {
            builder.append("<div class='meal-section'>")
                    .append("<h2>").append(mealType).append("</h2>")
                    .append("<ul>");
            details.forEach(detail -> builder.append("<li>✅ ").append(detail).append("</li>"));
            builder.append("</ul>")
                    .append("</div>");
        });

        builder.append("<div class='footer'>")
                .append("Enjoy your meals! Stay healthy & happy! 😊<br>")
                .append("<strong>HealthHub Team</strong><br><br>")
                .append("<div class='social-icons'>")
                .append("<a href='https://www.facebook.com/yourpage'><img src='https://cdn-icons-png.flaticon.com/512/733/733547.png' alt='Facebook'></a>")
                .append("<a href='https://www.instagram.com/yourpage'><img src='https://cdn-icons-png.flaticon.com/512/2111/2111463.png' alt='Instagram'></a>")
                .append("<a href='https://www.linkedin.com/company/yourpage'><img src='https://cdn-icons-png.flaticon.com/512/145/145807.png' alt='LinkedIn'></a>")
                .append("</div><br>")
                .append("<span style='font-size:12px; color:#999;'>HealthHub | Haldia, West Bengal | Contact Support</span>")
                .append("</div>")
                .append("</div></body></html>");

        return builder.toString();
    }


}