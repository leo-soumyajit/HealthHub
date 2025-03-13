package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.Entities.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
}
