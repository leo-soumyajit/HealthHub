package com.soumyajit.healthhub.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.soumyajit.healthhub.DTOS.PostDto;
import com.soumyajit.healthhub.Service.EmailSenderService;
import com.soumyajit.healthhub.Entities.Post;
import com.soumyajit.healthhub.Entities.User;
import com.soumyajit.healthhub.Exception.ResourceNotFound;
import com.soumyajit.healthhub.Exception.UnAuthorizedException;
import com.soumyajit.healthhub.Repository.PostRepository;
import com.soumyajit.healthhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final EmailSenderService emailSenderService;

    @Override
    @Transactional
    @CachePut(value = "postById", key = "#result.id")
    @CacheEvict(value = "allPosts", allEntries = true)
    public PostDto createPost(String title, String description, List<MultipartFile> images) throws IOException {
        log.info("Creating post with title , description and files");
        List<String> imageUrls = new ArrayList<>();

        if (images != null) {
            for (MultipartFile file : images) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();
                log.info("Uploaded image URL: {}", imageUrl);
                imageUrls.add(imageUrl);
            }
        }
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setLikes(0L);
        post.setImgOrVdos(imageUrls);
        User user = getCurrentUserWithPosts();
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        log.info("Saved post to the database");

        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (!u.getEmail().equals(user.getEmail())) { // Avoid emailing the admin who posted
                emailSenderService.sendPostNotification(u.getEmail(), user.getName(), post);
            }
        }
        return modelMapper.map(savedPost, PostDto.class);
    }




    @Override
    @Cacheable(value = "postById", key = "#postId")
    @CachePut(value = "postById", key = "#postId")
    public PostDto getPostById(Long postId) {
        log.info("Getting Post with id:{}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id : " + postId));
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    @Cacheable(value = "allPosts")
    @CachePut(value = "allPosts")
    public List<PostDto> getAllPosts() {
        log.info("Getting All Posts ");
        List<Post> post = postRepository.findAllPostsOrderedByCreationDateDesc();
        return post.stream()
                .map(post1 -> modelMapper.map(post1, PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CachePut(value = "postById", key = "#postId")
    @CacheEvict(value = "allPosts", allEntries = true)
    public PostDto postLikeById(Long postId) {
        log.info("Liking post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (user.getLikedPostIds().contains(postId)) {
            throw new RuntimeException("User has already liked this post");
        }

        post.setLikes(post.getLikes() + 1);
        user.getLikedPostIds().add(postId);

        Post savedPost = postRepository.save(post);
        userRepository.save(user);

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    @Transactional
    @CachePut(value = "postById", key = "#postId")
    @CacheEvict(value = "allPosts", allEntries = true)
    public PostDto removeLikeById(Long postId) {
        log.info("Removing like from post with id: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (!user.getLikedPostIds().contains(postId)) {
            throw new RuntimeException("User has not liked this post");
        }

        post.setLikes(post.getLikes() - 1);
        user.getLikedPostIds().remove(postId);

        Post savedPost = postRepository.save(post);
        userRepository.save(user);

        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    @Transactional
    @CachePut(value = "postById", key = "#postId")
    @CacheEvict(value = "allPosts", allEntries = true)
    public PostDto updatePostById(Long postId, String title, String description) {
        log.info("Updating Post with id: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (!user.getId().equals(post.getUser().getId())) {
            throw new UnAuthorizedException("Post does not belong to this user with id: " + user.getId());
        }

        post.setTitle(title);
        post.setDescription(description);
        post.setImgOrVdos(post.getImgOrVdos());
        post.setLikes(post.getLikes());

        Post updatedPost = postRepository.save(post);
        return modelMapper.map(updatedPost, PostDto.class);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"postById", "allPosts"}, allEntries = true)
    public void deletePostById(Long postId) {
        log.info("Deleting Post with id: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with Id: " + postId));

        User user = getCurrentUser();

        if (!user.getId().equals(post.getUser().getId())) {
            throw new UnAuthorizedException("Post does not belong to this user with id: " + user.getId());
        }

        postRepository.delete(post); // <-- ACTUAL delete
        log.info("Post deleted successfully");
    }





    @Override
    public List<PostDto> searchPosts(String keyword) {
        log.info("Searching post with keyword : {}", keyword);
        List<Post> posts = postRepository.findByTitleContainingOrDescriptionContainingOrderByCreationDateDesc(keyword, keyword);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional(readOnly = true)
    private User getCurrentUserWithPosts() {
        User currentUser = getCurrentUser();
        return userRepository.findByIdWithPosts(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFound("User not found"));
    }
}
