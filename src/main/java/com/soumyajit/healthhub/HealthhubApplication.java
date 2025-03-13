package com.soumyajit.healthhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HealthhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthhubApplication.class, args);
	}

}
