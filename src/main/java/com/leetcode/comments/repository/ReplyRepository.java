package com.leetcode.comments.repository;

import com.leetcode.comments.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    Page<Reply> findByCommentId(Integer commentId, Pageable pageable);
}
