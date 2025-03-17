// File: src/main/java/com/soumyajit/healthhub/DTOS/UserMealPlanDTO.java
package com.soumyajit.healthhub.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class UserMealPlanDTO {
    private Long id;
    private LocalDate planDate;
    private Map<String, Map<String, List<String>>> mealPlanContent;
    private boolean active;
    private String healthGoal; // New field for the heading

    public UserMealPlanDTO(Long id, LocalDate planDate, Map<String, Map<String, List<String>>> mealPlanContent, boolean active, String healthGoal) {
        this.id = id;
        this.planDate = planDate;
        this.mealPlanContent = mealPlanContent;
        this.active = active;
        this.healthGoal = healthGoal;
    }

    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public Map<String, Map<String, List<String>>> getMealPlanContent() { return mealPlanContent; }
    public void setMealPlanContent(Map<String, Map<String, List<String>>> mealPlanContent) { this.mealPlanContent = mealPlanContent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getHealthGoal() { return healthGoal; }
    public void setHealthGoal(String healthGoal) { this.healthGoal = healthGoal; }
}

