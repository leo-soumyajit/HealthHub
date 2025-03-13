// File: src/main/java/com/soumyajit/healthhub/Controller/MealPlanController.java
package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.Service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/mealplan")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @GetMapping
    public ResponseEntity<Map<String, Map<String, List<String>>>> getMealPlan(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String dietaryRestriction,
            @RequestParam(required = false) String ingredients) {
        if (userId == null) {
            userId = 1L;
        }
        Map<String, Map<String, List<String>>> structuredMealPlan = mealPlanService.generateStructuredMealPlan(userId, dietaryRestriction, ingredients);
        return ResponseEntity.ok(structuredMealPlan);
    }
}
