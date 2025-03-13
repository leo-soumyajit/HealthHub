// File: src/main/java/com/healthhub/service/HealthService.java
package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.HealthDataDTO;
import com.soumyajit.healthhub.Entities.HealthData;
import com.soumyajit.healthhub.Repository.HealthDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthDataRepository healthDataRepository;

    @Autowired
    private MailService mailService;

    // Process incoming health data: validate and persist to the database.
    public void processHealthData(HealthDataDTO dto) {
        if (dto.getHeartRate() == null || dto.getBloodPressure() == null) {
            throw new IllegalArgumentException("Heart rate and blood pressure must be provided.");
        }
        if (dto.getTimestamp() == null) {
            dto.setTimestamp(LocalDateTime.now());
        }
        HealthData data = new HealthData(dto.getUserId(), dto.getHeartRate(), dto.getBloodPressure(), dto.getTimestamp());
        healthDataRepository.save(data);
    }

    // Scheduled task: runs every minute to analyze stored health data and send alerts.
    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void analyzeHealthData() {
        List<HealthData> dataList = healthDataRepository.findAll();
        for (HealthData data : dataList) {
            // Example threshold: if heart rate is above 120 bpm, send an email alert.
            if (data.getHeartRate() != null && data.getHeartRate() > 120) {
                try {
                    mailService.sendAlertEmail(
                            "Health Alert for User " + data.getUserId(),
                            "Your heart rate of " + data.getHeartRate() + " bpm is above the safe threshold."
                    );
                } catch (MailException e) {
                    System.err.println("Failed to send alert email: " + e.getMessage());
                }
            }
            // Additional threshold checks can be added here.
        }
    }
}
