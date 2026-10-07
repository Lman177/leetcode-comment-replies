package com.leetcode.comments.service.impl;

import com.leetcode.comments.entity.Comment;
import com.leetcode.comments.entity.Post;
import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.mapper.DiscussionMapper;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.repository.CommentRepository;
import com.leetcode.comments.repository.PostRepository;
import com.leetcode.comments.repository.ReplyRepository;
import com.leetcode.comments.service.CommentService;
import com.leetcode.comments.service.DiscussionRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final DiscussionRateLimiter rateLimiter;
    private final DiscussionMapper discussionMapper;

    @Override
    @Transactional
    public CommentResponse addComment(int postId, CreateCommentRequest request) {
        // TODO: Apply rate limiting, verify the post, persist a
        //       Comment entity, and map it to CommentResponse.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(
                        "Post " + postId + " not found"
                    )
                );
        Comment comment = discussionMapper.toComment(request, post);
        Comment savedComment = commentRepository.save(comment);

        return discussionMapper.toCommentResponse(savedComment);
    }

    @Override
    @Transactional
    public ReplyResponse addReply(int postId, int commentId, CreateCommentRequest request) {
        // TODO: Apply rate limiting, verify that the comment belongs to the post,
        //       persist a Reply entity,
        //       and map it to ReplyResponse.

        throw new UnsupportedOperationException("TODO: implement addReply");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(int postId) {
        // TODO: Verify the post, query its comments/replies in creation order,
        //       and map the entities to response DTOs.
        throw new UnsupportedOperationException("TODO: implement getComments");
    }
}
