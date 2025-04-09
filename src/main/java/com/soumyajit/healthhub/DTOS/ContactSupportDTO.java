package com.soumyajit.healthhub.DTOS;

import lombok.Data;

@Data
public class ContactSupportDTO {
    private String name;
    private String email;
    private String subject;
    private String message;
}
