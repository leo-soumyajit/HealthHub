package com.soumyajit.healthhub.DTOS;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailsDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String profileImage;

    private List<ProfilePostDTOS> postsList;
}
