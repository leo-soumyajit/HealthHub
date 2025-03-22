package com.soumyajit.healthhub.Controller;


import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.DTOS.UserDetailsDTO;
import com.soumyajit.healthhub.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PutMapping("/change-details")
    public ResponseEntity<ApiResponse<String>> changeUserName(@RequestBody UserDetailsDTO userDTO) {
        userService.updateUserName(userDTO);
        ApiResponse<String> response = new ApiResponse<>("Details updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<UserDetailsDTO> getCurrentUser(){
        return ResponseEntity.ok(userService.getCurrentUser());
    }


    @PutMapping("/change-profileImage")
    public ResponseEntity<ApiResponse<String>> changeUserProfileImage(@RequestParam(name = "file")MultipartFile multipartFile) {
        userService.updateProfileImage(multipartFile);
        ApiResponse<String> response = new ApiResponse<>("Profile picture updated successfully");
        return ResponseEntity.ok(response);
    }



}
