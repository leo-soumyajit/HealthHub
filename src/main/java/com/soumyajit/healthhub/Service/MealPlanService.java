// File: src/main/java/com/soumyajit/healthhub/Service/MealPlanService.java
package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.MealPlanDTO;  // (Keep this if you still need the flat DTO elsewhere)
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final AIIntegrationService aiIntegrationService;

    // This method now returns a structured meal plan grouped by day and meal type.
    public Map<String, Map<String, List<String>>> generateStructuredMealPlan(Long userId, String dietaryRestriction, String ingredients) {
        String aiResponse = aiIntegrationService.getMealPlanSuggestion(dietaryRestriction, ingredients);
        return parseMealPlan(aiResponse);
    }

    // Parses the raw AI response into a nested structure.
    private Map<String, Map<String, List<String>>> parseMealPlan(String aiResponse) {
        Map<String, Map<String, List<String>>> mealPlan = new LinkedHashMap<>();
        String[] lines = aiResponse.split("\\n");
        String currentDay = null;
        String currentMealType = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // If line is a day header: e.g. "**Monday**"
            if (line.startsWith("**") && line.endsWith("**")) {
                currentDay = line.replace("**", "").trim();
                mealPlan.put(currentDay, new LinkedHashMap<>());
                currentMealType = null;
            }
            // If line is a meal type header: e.g. "* Breakfast: Spinach and tomato omelette..."
            else if (line.startsWith("*")) {
                String mealLine = line.substring(1).trim();
                String[] parts = mealLine.split(":", 2);
                currentMealType = parts[0].trim();
                // If currentDay hasn't been set, assign a default day.
                if (currentDay == null) {
                    currentDay = "Unspecified Day";
                    mealPlan.put(currentDay, new LinkedHashMap<>());
                }
                mealPlan.get(currentDay).put(currentMealType, new ArrayList<>());
                if (parts.length > 1) {
                    String detail = parts[1].trim();
                    if (!detail.isEmpty()) {
                        mealPlan.get(currentDay).get(currentMealType).add(detail);
                    }
                }
            }
            // If line is an additional detail line: e.g. "+ 2 eggs, 1/4 cup chopped spinach..."
            else if (line.startsWith("+")) {
                String detail = line.substring(1).trim();
                if (currentDay != null && currentMealType != null) {
                    mealPlan.get(currentDay).get(currentMealType).add(detail);
                }
            }
            // Optionally, handle any other lines (e.g., introductory header) if needed.
        }
        return mealPlan;
    }
}
