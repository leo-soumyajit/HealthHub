package com.soumyajit.healthhub.Repository;

import com.soumyajit.healthhub.Entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1% OR p.description LIKE %?2% ORDER BY p.createdAt DESC")
    List<Post> findByTitleContainingOrDescriptionContainingOrderByCreationDateDesc(String titleKeyword, String descriptionKeyword);


    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllPostsOrderedByCreationDateDesc();
}
