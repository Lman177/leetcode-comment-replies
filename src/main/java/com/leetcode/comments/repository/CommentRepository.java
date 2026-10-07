package com.leetcode.comments.repository;

import com.leetcode.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostIdOrderByIdAsc(Integer postId);

    Optional<Comment> findByIdAndPostId(Integer id, Integer postId);
}
