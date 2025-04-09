package com.soumyajit.healthhub.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodaysMealPlanDTO {
    private LocalDate date;
    private String day;
    private Map<String, List<String>> meals;
}
