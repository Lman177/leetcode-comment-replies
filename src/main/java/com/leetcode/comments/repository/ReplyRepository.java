package com.leetcode.comments.repository;

import com.leetcode.comments.entity.Comment;
import com.leetcode.comments.entity.Reply;
import com.leetcode.comments.model.ReplyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    List<Reply> findByCommentIdOrderByIdAsc(Integer commentId);

    List<ReplyResponse> findAllByComment_Id(Integer id);

    List<ReplyResponse> findByComment(Comment comment);
}
