package com.leetcode.comments.mapper;

import com.leetcode.comments.entity.Comment;
import com.leetcode.comments.entity.Post;
import com.leetcode.comments.entity.Reply;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.ReplyResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Builder;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DiscussionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Comment toComment(CreateCommentRequest request, Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Reply toReply(CreateCommentRequest request, Comment comment);

    @Mapping(target = "postId", source = "post.id")
    CommentResponse toCommentResponse(Comment comment);

    @Mapping(target = "postId", source = "comment.post.id")
    @Mapping(target = "commentId", source = "comment.id")
    ReplyResponse toReplyResponse(Reply reply);
}
