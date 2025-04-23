package com.soumyajit.healthhub.DTOS;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProfilePostDTOS {
    private Long id;
    private String title;
    private String description;
    private List<String> imgOrVdos;
    private String userName;
    private Long userId;

}
