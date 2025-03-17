package com.soumyajit.healthhub.DTOS;

import java.util.List;
import java.util.Map;

public class MealPlanResponse {
    private String healthGoal;
    private Map<String, Map<String, List<String>>> mealPlan;

    public String getHealthGoal() {
        return healthGoal;
    }

    public void setHealthGoal(String healthGoal) {
        this.healthGoal = healthGoal;
    }

    public Map<String, Map<String, List<String>>> getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(Map<String, Map<String, List<String>>> mealPlan) {
        this.mealPlan = mealPlan;
    }
}
