package com.soumyajit.healthhub.Advices;

import lombok.*;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private HttpStatus status;
    private String message;
}
