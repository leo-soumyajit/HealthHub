package com.soumyajit.healthhub.Controller;

import com.soumyajit.healthhub.DTOS.PostDto;
import com.soumyajit.healthhub.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/createPost", consumes = {"multipart/form-data"})
    public ResponseEntity<PostDto> createPost(@RequestParam("title") String title,
                                              @RequestParam("description") String description,
                                              @RequestParam(value = "imageFiles", required = false) List<MultipartFile> images) throws IOException {
        return ResponseEntity.ok(postService.createPost(title, description, images));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId){
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping()
   // @CrossOrigin(origins = "http://localhost:63342")
    public ResponseEntity<List<PostDto>> getAllPost(){
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostDto> postLikeById(@PathVariable Long postId){
        return ResponseEntity.ok(postService.postLikeById(postId));
    }

    @PostMapping("/{postId}/removelike")
    public ResponseEntity<PostDto> removeLike(@PathVariable Long postId) {
        PostDto postDto = postService.removeLikeById(postId);
        return ResponseEntity.ok(postDto);
    }


    @PutMapping(value = "/{postId}/update")
    public ResponseEntity<PostDto> updatePostById(@PathVariable Long postId,
                                                  @RequestParam("title") String title,
                                                  @RequestParam("description") String description)
                                                  {
        return ResponseEntity.ok(postService.updatePostById(postId,title,description));
    }
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<Void> deletePostById(@PathVariable Long postId){
        postService.deletePostById(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>>searchPosts(@RequestParam String keyword){
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

}
