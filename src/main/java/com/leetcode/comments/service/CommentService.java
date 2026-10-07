package com.leetcode.comments.service;

import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.ReplyResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(int postId, CreateCommentRequest request);

    ReplyResponse addReply(int postId, int commentId, CreateCommentRequest request);

    List<CommentResponse> getComments(int postId);
}
