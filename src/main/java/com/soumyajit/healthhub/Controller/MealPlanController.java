// File: src/main/java/com/soumyajit/healthhub/Controller/MealPlanController.java
package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestParam(required = false) String dietaryRestriction,
            @RequestParam(required = false) String ingredients) {

        // Retrieve the authenticated user's details from the security context.
        User userDetails = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Map<String, Map<String, List<String>>> structuredMealPlan =
                mealPlanService.generateStructuredMealPlan(userId, dietaryRestriction, ingredients);
        return ResponseEntity.ok(structuredMealPlan);
    }

}
