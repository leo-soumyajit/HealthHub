package com.soumyajit.healthhub.Service;

import com.soumyajit.healthhub.DTOS.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostDto createPost(String title ,  String description ,List<MultipartFile> imageFiles) throws IOException;

    PostDto getPostById(Long postId);

    List<PostDto> getAllPosts();

    PostDto postLikeById(Long postId);

    PostDto updatePostById(Long postId,String title ,  String description);

    void deletePostById(Long postId);


    List<PostDto> searchPosts(String keyword);

    PostDto removeLikeById(Long postId);
}
