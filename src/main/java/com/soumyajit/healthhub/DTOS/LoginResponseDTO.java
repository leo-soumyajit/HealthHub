package com.soumyajit.healthhub.DTOS;

import com.soumyajit.healthhub.Entities.Enums.Roles;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class LoginResponseDTO {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private Set<Roles> roles;

    public LoginResponseDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LoginResponseDTO(Long id, String accessToken) {
        this.id = id;
        this.accessToken = accessToken;
    }

    public LoginResponseDTO(Long id, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
    }

    public LoginResponseDTO(Long id, String accessToken, String refreshToken, Set<Roles> roles) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.roles = roles;
    }
}
