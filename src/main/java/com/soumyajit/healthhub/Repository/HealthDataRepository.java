package com.soumyajit.healthhub.Repository;

import com.soumyajit.healthhub.Entities.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData,Long> {
}
