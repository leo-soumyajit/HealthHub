package com.soumyajit.healthhub.Service;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class PingScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    // Every 30 seconds
    @Scheduled(cron = "*/30 * * * * *")
    public void pingHealthEndpoint() {
        try {
            String url = "https://healthhub-7656.onrender.com/actuator/health";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Health ping response: " + response);
        } catch (Exception e) {
            System.err.println("Health ping failed: " + e.getMessage());
        }
    }
}