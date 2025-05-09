package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.UserDetailsDTO;
import com.soumyajit.healthhub.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();


    void updateUserName(UserDetailsDTO userDTO);

    void updateProfileImage(MultipartFile multipartFile);

    UserDetailsDTO getCurrentUser();
}
