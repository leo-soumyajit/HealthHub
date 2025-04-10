package com.soumyajit.healthhub.DTOS;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DoctorApplicationRequest {
    private String name;
    private String email;
    private String education;
    private String experiences;
    private String registrationNumber;

    private MultipartFile licenseDocument;
    private MultipartFile hospitalIdImage;
}
