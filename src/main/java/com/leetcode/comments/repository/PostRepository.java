package com.leetcode.comments.repository;

import com.leetcode.comments.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
