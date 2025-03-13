package com.soumyajit.healthhub.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soumyajit.healthhub.DTOS.UserMealPlanDTO;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Entities.UserMealPlan;
import com.soumyajit.healthhub.Repository.UserMealPlanRepository;
import com.soumyajit.healthhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final AIIntegrationService aiIntegrationService;
    private final UserMealPlanRepository userMealPlanRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generates a structured meal plan by calling the AI integration, parses the response,
     * and saves the meal plan as a JSON string for the given user.
     * The meal plan is saved with the active flag set to false.
     */
    public Map<String, Map<String, List<String>>> generateStructuredMealPlan(Long userId, String dietaryRestriction, String ingredients) {
        // Get the AI response based on the dietary restrictions and available ingredients.
        String aiResponse = aiIntegrationService.getMealPlanSuggestion(dietaryRestriction, ingredients);
        Map<String, Map<String, List<String>>> structuredMealPlan = parseMealPlan(aiResponse);

        try {
            // Convert the structured plan to a JSON string.
            String mealPlanJson = objectMapper.writeValueAsString(structuredMealPlan);
            // Retrieve the User entity from the database.
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            // Create a new meal plan entry (saved as inactive by default).
            UserMealPlan userMealPlan = new UserMealPlan(user, LocalDate.now(), mealPlanJson, false);
            userMealPlanRepository.save(userMealPlan);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return structuredMealPlan;
    }

    /**
     * Activates a saved meal plan for the user.
     * First, any currently active meal plan for the user is deactivated.
     * Then, the specified meal plan is set as active.
     */
    public void activateMealPlanForUser(Long userId, Long mealPlanId) {
        // Deactivate any currently active meal plan for the user.
        userMealPlanRepository.findByUserIdAndActiveTrue(userId).ifPresent(activePlan -> {
            activePlan.setActive(false);
            userMealPlanRepository.save(activePlan);
        });
        // Retrieve the selected meal plan by ID.
        Optional<UserMealPlan> mealPlanOpt = userMealPlanRepository.findById(mealPlanId);
        if (mealPlanOpt.isPresent()) {
            UserMealPlan selectedPlan = mealPlanOpt.get();
            // Check if the meal plan belongs to the current user.
            if (!selectedPlan.getUser().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized: Meal plan does not belong to the user");
            }
            selectedPlan.setActive(true);
            userMealPlanRepository.save(selectedPlan);
        } else {
            throw new RuntimeException("Meal plan not found");
        }
    }

    public List<UserMealPlanDTO> getMealPlanDTOsForAuthenticatedUser() {
        // Retrieve the authenticated user from the security context.
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserMealPlan> mealPlans = userMealPlanRepository.findByUser(user);
        return mealPlans.stream().map(plan -> {
            Map<String, Map<String, List<String>>> structuredPlan = null;
            try {
                structuredPlan = objectMapper.readValue(plan.getMealPlanContent(),
                        new TypeReference<Map<String, Map<String, List<String>>>>() {});
            } catch (Exception e) {
                e.printStackTrace();
                structuredPlan = null; // or an empty map if preferred
            }
            return new UserMealPlanDTO(
                    plan.getId(),
                    plan.getPlanDate(),
                    structuredPlan,
                    plan.isActive()
            );
        }).collect(Collectors.toList());
    }


    /**
     * Parses the raw AI response into a nested structure.
     * Expected format: Day headers (e.g., "**Monday**") and meal type headers (e.g., "* Breakfast: ..."),
     * with additional detail lines starting with "+".
     */
    private Map<String, Map<String, List<String>>> parseMealPlan(String aiResponse) {
        Map<String, Map<String, List<String>>> mealPlan = new LinkedHashMap<>();
        List<String> expectedDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        // Initialize the map with the expected days.
        for (String day : expectedDays) {
            mealPlan.put(day, new LinkedHashMap<>());
        }
        String[] lines = aiResponse.split("\\n");
        String currentDay = null;
        String currentMealType = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            // Check for day header (e.g., "**Monday**")
            if (line.startsWith("**") && line.endsWith("**")) {
                String dayHeader = line.replace("**", "").trim();
                if (expectedDays.contains(dayHeader)) {
                    currentDay = dayHeader;
                } else {
                    currentDay = "Unspecified Day";
                }
            }
            // Check for meal type header (e.g., "* Breakfast: ...")
            else if (line.startsWith("*")) {
                String mealLine = line.substring(1).trim();
                String[] parts = mealLine.split(":", 2);
                currentMealType = parts[0].trim();
                if (currentDay == null || !mealPlan.containsKey(currentDay)) {
                    currentDay = expectedDays.get(0);
                }
                mealPlan.get(currentDay).putIfAbsent(currentMealType, new ArrayList<>());
                if (parts.length > 1) {
                    String detail = parts[1].trim();
                    if (!detail.isEmpty()) {
                        mealPlan.get(currentDay).get(currentMealType).add(detail);
                    }
                }
            }
            // Check for additional detail lines (e.g., "+ ...")
            else if (line.startsWith("+")) {
                String detail = line.substring(1).trim();
                if (currentDay != null && currentMealType != null) {
                    mealPlan.get(currentDay).get(currentMealType).add(detail);
                }
            }
        }
        return mealPlan;
    }
}
