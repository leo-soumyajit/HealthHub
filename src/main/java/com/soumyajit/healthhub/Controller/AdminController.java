package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.Service.adminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final adminService adminService;

    // Promote user to admin
    @PostMapping("/make-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> makeUserAdmin(@PathVariable Long userId) {
        adminService.makeUserAdmin(userId);
        return ResponseEntity.ok(new ApiResponse<>("User promoted to ADMIN successfully 🚀"));
    }

    @PostMapping("/make-doctor/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> makeUserDoctor(@PathVariable Long userId) {
        adminService.makeUserDoctor(userId);
        return ResponseEntity.ok(new ApiResponse<>("User promoted to DOCTOR successfully 🚀"));
    }


}
