package com.soumyajit.healthhub.DTOS;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTOS {
    private Long id;
    private String userName;
    private List<ProfilePostDTOS> postsList;
    private String bio;
    private String profileImage;

}
