package com.soumyajit.healthhub.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.soumyajit.healthhub.DTOS.UserDTO;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Exception.ResourceNotFound;
import com.soumyajit.healthhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;
    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;  // ✅ Now it correctly fetches users from the database
    }

    @Override
    @Cacheable(cacheNames = "users", key = "T(java.util.Objects).hash(T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getPrincipal().id)")
    public void updateUserName(UserDTO userDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findById(user.getId()).
                orElseThrow(() -> new ResourceNotFound("User with this id not found"));
        //if (user.getId().equals(u))
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        userRepository.save(user);
    }

    @Override
    public void updateProfileImage(MultipartFile file) {
        // Retrieve authenticated user's details inside the service.
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Upload the file to Cloudinary.
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            // Update the user's profile picture URL.
            user.setProfileImage(imageUrl);
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }







    @Override
    @Cacheable(cacheNames = "users", key = "T(com.soumyajit.healthhub.Utils.SecurityUtil).getCurrentUserId()")
    //@Cacheable(cacheNames = "users", key = "#user.id")
    public UserDTO getCurrentUser() {
        log.info("Fetching current user from DB");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetched user with id {}", user.getId());
        return modelMapper.map(user, UserDTO.class);
    }









    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }



}
