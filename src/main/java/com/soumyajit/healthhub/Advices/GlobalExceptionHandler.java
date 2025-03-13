package com.soumyajit.healthhub.Advices;

//import io.jsonwebtoken.JwtException;
import com.soumyajit.healthhub.Exception.ResourceNotFound;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiError> noEmployeeFound(ResourceNotFound exception){
        ApiError apiError = ApiError.builder().status(HttpStatus.NOT_FOUND).message(exception.getMessage()).build();
        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);
    }

//    private ResponseEntity<ApiError> buildErrorResponseEntity(ApiError apiError) {
//        return new ResponseEntity<>(new ApiResponse<>(apiError),apiError.getStatus());
//    }




    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> HandleJwtException(JwtException exception){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> HandleAuthenticationException(AuthenticationException exception){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> HandleAccessDeniedException(AccessDeniedException exception){
        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);
    }


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<?>> invalidInputs(MethodArgumentNotValidException exception){
//        List<String> errors = exception.getBindingResult()
//                .getAllErrors()
//                .stream().map(objectError -> objectError.getDefaultMessage())
//                .collect(Collectors.toList());
//
//        ApiError apiError = ApiError
//                .builder().status(HttpStatus.BAD_REQUEST)
//                .message(errors.toString()).build();
//        return buildErrorResponseEntity(apiError);
//    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> InternalServererror(RuntimeException e){
        ApiError apiError = ApiError
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.FORBIDDEN);
    }


}
