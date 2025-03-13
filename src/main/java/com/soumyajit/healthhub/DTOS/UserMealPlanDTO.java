// File: src/main/java/com/soumyajit/healthhub/DTOS/UserMealPlanDTO.java
package com.soumyajit.healthhub.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMealPlanDTO {
    private Long id;
    private LocalDate planDate;
    // This field now holds the parsed meal plan structure.
    private Map<String, Map<String, List<String>>> mealPlanContent;
    private boolean active;
}
