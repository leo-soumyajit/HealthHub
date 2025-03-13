package com.soumyajit.healthhub.DTOS;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class HealthDataDTO {

    private Long userId;
    private Double heartRate;
    private Double bloodPressure;
    private LocalDateTime timestamp;

    public HealthDataDTO() {}

    public HealthDataDTO(Long userId, Double heartRate, Double bloodPressure, LocalDateTime timestamp) {
        this.userId = userId;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.timestamp = timestamp;
    }


}
