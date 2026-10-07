package com.leetcode.comments.service;

import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.ReplyResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(int postId, String userId, String content);

    ReplyResponse addReply(int postId, int commentId, String userId, String content);

    List<CommentResponse> getComments(int postId);
}
