package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.Advices.ApiResponse;
import com.soumyajit.healthhub.DTOS.ContactSupportDTO;
import com.soumyajit.healthhub.Service.ContactSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class ContactSupportController {

    private final ContactSupportService contactSupportService;

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<String>> contactSupport(@RequestBody ContactSupportDTO dto) {
        try {
            contactSupportService.sendSupportMessage(dto);
            return ResponseEntity.ok(new ApiResponse<>("✅ Message sent to support successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("❌ Failed to send message: " + e.getMessage()));
        }
    }
}

