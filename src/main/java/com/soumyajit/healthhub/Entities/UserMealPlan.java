package com.soumyajit.healthhub.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate planDate;

    @Lob
    private String mealPlanContent;

    private boolean active;

    public UserMealPlan(User user, LocalDate planDate, String mealPlanContent, boolean active) {
        this.user = user;
        this.planDate = planDate;
        this.mealPlanContent = mealPlanContent;
        this.active = active;
    }
}
