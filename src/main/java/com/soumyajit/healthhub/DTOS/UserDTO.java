package com.soumyajit.healthhub.DTOS;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
}
