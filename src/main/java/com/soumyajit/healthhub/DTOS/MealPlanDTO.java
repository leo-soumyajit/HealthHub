package com.soumyajit.healthhub.DTOS;

import lombok.Data;

@Data
public class MealPlanDTO {
    private String mealName;
    private String description;

    public MealPlanDTO() {}

    public MealPlanDTO(String mealName, String description) {
        this.mealName = mealName;
        this.description = description;
    }
}
