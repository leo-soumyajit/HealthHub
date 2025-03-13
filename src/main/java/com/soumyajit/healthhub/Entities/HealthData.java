// File: src/main/java/com/healthhub/entity/HealthData.java
package com.soumyajit.healthhub.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class HealthData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Double heartRate;
    private Double bloodPressure;
    private LocalDateTime timestamp;

    public HealthData() {}

    public HealthData(Long userId, Double heartRate, Double bloodPressure, LocalDateTime timestamp) {
        this.userId = userId;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.timestamp = timestamp;
    }

}
