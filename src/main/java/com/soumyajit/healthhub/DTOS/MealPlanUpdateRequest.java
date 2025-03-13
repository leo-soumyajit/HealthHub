package com.soumyajit.healthhub.DTOS;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MealPlanUpdateRequest {
    private String planName;
    private Map<String, List<String>> meals;
}
