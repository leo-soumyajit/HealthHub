package com.soumyajit.healthhub.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_meal_plan")
public class UserMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Lob
    @Column(name = "meal_plan_content", columnDefinition = "TEXT", nullable = false)
    private String mealPlanContent;

    @Column(nullable = false)
    private boolean active;

    public UserMealPlan(User user, LocalDate planDate, String mealPlanContent, boolean active) {
        this.user = user;
        this.planDate = planDate;
        this.mealPlanContent = mealPlanContent;
        this.active = active;
    }
}
