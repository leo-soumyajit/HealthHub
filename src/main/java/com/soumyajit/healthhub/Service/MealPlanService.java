package com.soumyajit.healthhub.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soumyajit.healthhub.DTOS.UserMealPlanDTO;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Entities.UserMealPlan;
import com.soumyajit.healthhub.Exception.ResourceNotFound;
import com.soumyajit.healthhub.Repository.UserMealPlanRepository;
import com.soumyajit.healthhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    public Map<String, Map<String, List<String>>> generateStructuredMealPlan(Long userId,
                                                                             String dietaryRestriction, String ingredients, String healthGoal) {
        // Get the AI response based on dietary restrictions, ingredients, and the health goal.
        String aiResponse = aiIntegrationService.getMealPlanSuggestion(dietaryRestriction, ingredients, healthGoal);
        Map<String, Map<String, List<String>>> structuredMealPlan = parseMealPlan(aiResponse);

        try {
            // Convert the structured plan to a JSON string.
            String mealPlanJson = objectMapper.writeValueAsString(structuredMealPlan);
            // Retrieve the User entity from the database.
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFound("User not found"));
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
            throw new ResourceNotFound("Meal plan not found");
        }
    }

    public void deactivateMealPlanForUser(Long userId, Long mealPlanId) {
        // Retrieve the selected meal plan by ID.
        Optional<UserMealPlan> mealPlanOpt = userMealPlanRepository.findById(mealPlanId);

        if (mealPlanOpt.isPresent()) {
            UserMealPlan selectedPlan = mealPlanOpt.get();

            // Check if the meal plan belongs to the current user.
            if (!selectedPlan.getUser().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized: Meal plan does not belong to the user");
            }

            // Deactivate the meal plan
            selectedPlan.setActive(false);
            userMealPlanRepository.save(selectedPlan);
        } else {
            throw new ResourceNotFound("Meal plan not found");
        }
    }


    public void deleteMealPlanForUser(Long mealPlanId, Long userId) {
        // Fetch the meal plan from the database
        UserMealPlan mealPlan = userMealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new ResourceNotFound("Meal plan not found"));

        // Ensure the meal plan belongs to the authenticated user
        if (!mealPlan.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Meal plan does not belong to you");
        }

        // Delete the meal plan
        userMealPlanRepository.delete(mealPlan);
        log.info("Meal plan {} deleted for user {}", mealPlanId, userId);
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

    public String updateMealPlan(Long mealPlanId, Long userId, Map<String, Map<String, List<String>>> updatedMealPlanContent) {
        UserMealPlan existingMealPlan = userMealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        // Ensure only the owner can edit
        if (!existingMealPlan.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to edit this meal plan");
        }

        try {
            // Convert existing meal plan JSON to Map
            Map<String, Map<String, List<String>>> existingMealPlanContent =
                    objectMapper.readValue(existingMealPlan.getMealPlanContent(), new TypeReference<>() {});

            // Merge existing plan with updates (only modifying provided days)
            for (String day : updatedMealPlanContent.keySet()) {
                existingMealPlanContent.put(day, updatedMealPlanContent.get(day));
            }

            // Convert back to JSON and save
            existingMealPlan.setMealPlanContent(objectMapper.writeValueAsString(existingMealPlanContent));
            userMealPlanRepository.save(existingMealPlan);

            return "Meal plan updated successfully";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing meal plan data", e);
        }
    }




    /**
     * Parses the raw AI response into a nested structure.
     * Expected format: Day headers (e.g., "**Monday**") and meal type headers (e.g., "* Breakfast: ..."),
     * with additional detail lines starting with "+".
     */
    private Map<String, Map<String, List<String>>> parseMealPlan(String aiResponse) {
        // Define expected days and meal types.
        List<String> expectedDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        Set<String> expectedMealTypes = new HashSet<>(Arrays.asList("Breakfast", "Snack", "Lunch", "Dinner"));

        // Initialize the meal plan map with expected days.
        Map<String, Map<String, List<String>>> mealPlan = new LinkedHashMap<>();
        for (String day : expectedDays) {
            mealPlan.put(day, new LinkedHashMap<>());
        }

        String[] lines = aiResponse.split("\\n");
        String currentDay = null;
        String currentMealType = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Remove all asterisks for easier matching.
            String cleanLine = line.replace("*", "").trim();

            // If cleanLine exactly matches an expected day, update currentDay.
            if (expectedDays.contains(cleanLine)) {
                currentDay = cleanLine;
                continue;
            }

            // If the line starts with "*" then it's a meal type header.
            if (line.startsWith("*")) {
                // Remove the leading "*" and split by ":" to see if inline detail exists.
                String mealLine = line.substring(1).trim();
                String[] parts = mealLine.split(":", 2);
                // Clean meal type key by removing extra asterisks.
                currentMealType = parts[0].replace("*", "").trim();
                // Default to first expected day if currentDay is not set.
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
                continue;
            }

            // If the line starts with "+", it's an additional detail.
            if (line.startsWith("+")) {
                String detail = line.substring(1).trim();
                if (currentDay != null && currentMealType != null) {
                    mealPlan.get(currentDay).get(currentMealType).add(detail);
                }
                continue;
            }

            // Otherwise, treat the line as an additional detail.
            if (currentDay != null && currentMealType != null) {
                mealPlan.get(currentDay).get(currentMealType).add(line);
            }
        }

        // Post-process each day to merge keys not in expectedMealTypes into "Tips"
        for (String day : mealPlan.keySet()) {
            Map<String, List<String>> dayMeals = mealPlan.get(day);
            List<String> tips = new ArrayList<>();
            // If there is already a "Tips" key, add its content (after cleaning).
            if (dayMeals.containsKey("Tips")) {
                for (String tip : dayMeals.get("Tips")) {
                    if (!tip.trim().equals("**") && !tip.trim().isEmpty()) {
                        tips.add(tip);
                    }
                }
                dayMeals.remove("Tips");
            }
            // For any key that is not an expected meal type, consider it as a tip.
            for (String key : new ArrayList<>(dayMeals.keySet())) {
                if (!expectedMealTypes.contains(key)) {
                    // Add the key itself if it's meaningful.
                    if (!key.trim().isEmpty()) {
                        tips.add(key);
                    }
                    // Add any details under that key.
                    List<String> details = dayMeals.get(key);
                    if (details != null) {
                        for (String detail : details) {
                            if (!detail.trim().isEmpty()) {
                                tips.add(detail);
                            }
                        }
                    }
                    dayMeals.remove(key);
                }
            }
            // Reorder keys so that expected meal types appear first.
            LinkedHashMap<String, List<String>> orderedDayMeals = new LinkedHashMap<>();
            List<String> desiredOrder = Arrays.asList("Breakfast", "Snack", "Lunch", "Dinner");
            for (String type : desiredOrder) {
                if (dayMeals.containsKey(type)) {
                    orderedDayMeals.put(type, dayMeals.get(type));
                }
            }
            // Add any remaining keys (if any)
            for (String key : dayMeals.keySet()) {
                if (!orderedDayMeals.containsKey(key)) {
                    orderedDayMeals.put(key, dayMeals.get(key));
                }
            }
            // Finally, add the "Tips" key if tips exist.
            if (!tips.isEmpty()) {
                orderedDayMeals.put("Tips", tips);
            }
            mealPlan.put(day, orderedDayMeals);
        }

        return mealPlan;
    }



}