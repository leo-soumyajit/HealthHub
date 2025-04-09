// File: src/main/java/com/soumyajit/healthhub/Repository/UserMealPlanRepository.java
package com.soumyajit.healthhub.Repository;

import com.soumyajit.healthhub.Entities.UserMealPlan;
import com.soumyajit.healthhub.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMealPlanRepository extends JpaRepository<UserMealPlan, Long> {

    Optional<UserMealPlan> findByUserIdAndActiveTrue(Long userId);

    List<UserMealPlan> findByUser(User user);

    List<UserMealPlan> findAllByUserIdAndActiveTrue(Long userId);
}
