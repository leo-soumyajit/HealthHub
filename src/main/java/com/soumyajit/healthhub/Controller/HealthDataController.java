package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.DTOS.HealthDataDTO;
import com.soumyajit.healthhub.Service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthDataController {


    private final HealthService healthService;

    @PostMapping("/data")
    public ResponseEntity<String> submitHealthData(@RequestBody HealthDataDTO healthDataDTO) {
        healthService.processHealthData(healthDataDTO);
        return ResponseEntity.ok("Health data received and processed.");
    }

}
