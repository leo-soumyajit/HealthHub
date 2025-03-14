package com.soumyajit.healthhub.Controller;


import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.DTOS.UserDTO;
import com.soumyajit.healthhub.Service.UserService;
import com.soumyajit.healthhub.Service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PutMapping("/change-name")
    public ResponseEntity<ApiResponse<String>> changeUserName(@RequestBody UserDTO userDTO) {
        userService.updateUserName(userDTO);
        ApiResponse<String> response = new ApiResponse<>("Name updated successfully");
        return ResponseEntity.ok(response);
    }


    @PutMapping("/change-profileImage")
    public ResponseEntity<ApiResponse<String>> changeUserProfileImage(@RequestParam(name = "file")MultipartFile multipartFile) {
        userService.updateProfileImage(multipartFile);
        ApiResponse<String> response = new ApiResponse<>("Profile picture updated successfully");
        return ResponseEntity.ok(response);
    }



}
