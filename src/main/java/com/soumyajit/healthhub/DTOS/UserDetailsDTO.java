package com.soumyajit.healthhub.DTOS;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String profileImage;
}
