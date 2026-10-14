package com.leetcode.comments.service.impl;

import com.leetcode.comments.entity.Comment;
import com.leetcode.comments.entity.Post;
import com.leetcode.comments.entity.Reply;
import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.exception.ValidationException;
import com.leetcode.comments.mapper.DiscussionMapper;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.PageResponse;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.repository.CommentRepository;
import com.leetcode.comments.repository.PostRepository;
import com.leetcode.comments.repository.ReplyRepository;
import com.leetcode.comments.service.CommentService;
import com.leetcode.comments.service.DiscussionRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        rateLimiter.checkAllowed(request.userId());

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
        rateLimiter.checkAllowed(request.userId());

        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new NotFoundException(
                                "Post " + postId + " not found"
                        )
                );
        Reply reply = discussionMapper.toReply(request, comment);
        Reply saveReply = replyRepository.save(reply);

        return discussionMapper.toReplyResponse(saveReply);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getComments(int postId, int page, int size) {
        Pageable pageable = pageRequest(page, size);
        // Verify the post, then query comments in creation order.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(
                                "Post " + postId + " not found"
                        )
                );
        return PageResponse.from(commentRepository.findByPostId(post.getId(), pageable)
                .map(discussionMapper::toCommentResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReplyResponse> getReplies(int commentId, int page, int size) {
        Pageable pageable = pageRequest(page, size);
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(
                        "Comment " + commentId + " not found"
                ));

        return PageResponse.from(replyRepository.findByCommentId(commentId, pageable)
                .map(discussionMapper::toReplyResponse));
    }

    private Pageable pageRequest(int page, int size) {
        if (page < 0) {
            throw new ValidationException("page must be greater than or equal to 0");
        }
        if (size < 1 || size > 100) {
            throw new ValidationException("size must be between 1 and 100");
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
    }
}
