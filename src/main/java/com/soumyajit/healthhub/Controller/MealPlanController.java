// File: src/main/java/com/soumyajit/healthhub/Controller/MealPlanController.java
package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.DTOS.MealPlanUpdateRequest;
import com.soumyajit.healthhub.DTOS.UserMealPlanDTO;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Entities.UserMealPlan;
import com.soumyajit.healthhub.Service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = true) String healthGoal) {

        // Retrieve the authenticated user's details from the security context.
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        // Pass the extra healthGoal parameter to the service
        Map<String, Map<String, List<String>>> structuredMealPlan =
                mealPlanService.generateStructuredMealPlan(userId, dietaryRestriction, ingredients, healthGoal);
        return ResponseEntity.ok(structuredMealPlan);
    }



    @PutMapping("/{mealPlanId}/activate")
    public ResponseEntity<ApiResponse<String>> activateMealPlan(@PathVariable Long mealPlanId) {
        // Retrieve authenticated user
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        mealPlanService.activateMealPlanForUser(userId, mealPlanId);

        // Wrap your message in ApiResponse
        ApiResponse<String> response = new ApiResponse<>("Meal plan activated successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{mealPlanId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMealPlan(@PathVariable Long mealPlanId) {
        // Retrieve authenticated user
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        mealPlanService.deactivateMealPlanForUser(userId, mealPlanId);

        // Wrap response message
        ApiResponse<String> response = new ApiResponse<>("Meal plan deactivated successfully");
        return ResponseEntity.ok(response);
    }



    @GetMapping("/myMealPlans")
    public ResponseEntity<ApiResponse<List<UserMealPlanDTO>>> getAllMealPlansForUser() {
        List<UserMealPlanDTO> dtos = mealPlanService.getMealPlanDTOsForAuthenticatedUser();
        ApiResponse<List<UserMealPlanDTO>> response = new ApiResponse<>(dtos);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{mealPlanId}/edit")
    public ResponseEntity<ApiResponse<String>> updateMealPlan(
            @PathVariable Long mealPlanId,
            @RequestBody Map<String, Map<String, List<String>>> updatedMealPlanContent) {

        // Get the logged-in user
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        String message = mealPlanService.updateMealPlan(mealPlanId, userId, updatedMealPlanContent);
        return ResponseEntity.ok(new ApiResponse<>(message));
    }


    @DeleteMapping("/{mealPlanId}")
    public ResponseEntity<ApiResponse<String>> deleteMealPlan(@PathVariable Long mealPlanId) {
        // Retrieve the authenticated user from the security context
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        // Call the service to delete the meal plan
        mealPlanService.deleteMealPlanForUser(mealPlanId, userId);

        // Wrap the success message in ApiResponse and return
        ApiResponse<String> response = new ApiResponse<>("Meal plan deleted successfully");
        return ResponseEntity.ok(response);
    }




}