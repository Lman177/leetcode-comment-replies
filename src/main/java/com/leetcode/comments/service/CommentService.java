package com.leetcode.comments.service;

import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.PageResponse;
import com.leetcode.comments.model.ReplyResponse;

public interface CommentService {
    CommentResponse addComment(int postId, CreateCommentRequest request);

    ReplyResponse addReply(int postId, int commentId, CreateCommentRequest request);

    PageResponse<CommentResponse> getComments(int postId, int page, int size);

    PageResponse<ReplyResponse> getReplies(int commentId, int page, int size);
}
