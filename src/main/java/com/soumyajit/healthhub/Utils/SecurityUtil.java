package com.soumyajit.healthhub.Utils;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import com.soumyajit.healthhub.Entities.User; // adjust the package according to your project structure

public class SecurityUtil {
    @CachePut(cacheNames = "user",key = "#userId")
    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            Long userId = ((User) principal).getId();
            System.out.println("Cache key (user id): " + userId);
            return userId;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}
