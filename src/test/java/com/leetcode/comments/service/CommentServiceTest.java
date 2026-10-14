package com.leetcode.comments.service;

import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.exception.RateLimitExceededException;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateCommentRequest;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.repository.CommentRepository;
import com.leetcode.comments.repository.ReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CommentServiceTest {
    @Autowired
    private CommentService service;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        replyRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void createsOrderedCommentsAndRepliesWithIndependentIds() {
        CommentResponse first = service.addComment(10, request("u1", "First"));
        CommentResponse second = service.addComment(10, request("u2", "Second"));
        ReplyResponse reply = service.addReply(10, first.getId(), request("u3", "Reply"));

        List<CommentResponse> thread = service.getComments(10, 0, 20).content();
        assertEquals(List.of(first.getId(), second.getId()),
                thread.stream().map(CommentResponse::getId).toList());
        assertEquals(first.getId(), reply.getCommentId());
        assertEquals(1, service.getReplies(first.getId(), 0, 20).content().size());
    }

    @Test
    void isolatesPostsAndRejectsReplyThroughWrongPost() {
        CommentResponse comment = service.addComment(10, request("u1", "hello"));
        assertEquals(0, service.getComments(20, 0, 20).content().size());
        assertThrows(NotFoundException.class,
                () -> service.addReply(20, comment.getId(), request("u2", "wrong post")));
    }

    @Test
    void returnedObjectsCannotMutateStoredState() {
        CommentResponse created = service.addComment(10, request("u1", "original"));
        service.addReply(10, created.getId(), request("u2", "original reply"));

        List<CommentResponse> result = service.getComments(10, 0, 20).content();
        List<ReplyResponse> replies = service.getReplies(created.getId(), 0, 20).content();
        result.get(0).setContent("changed");
        replies.get(0).setContent("changed reply");

        List<CommentResponse> fresh = service.getComments(10, 0, 20).content();
        List<ReplyResponse> freshReplies = service.getReplies(created.getId(), 0, 20).content();
        assertEquals("original", fresh.get(0).getContent());
        assertEquals("original reply", freshReplies.get(0).getContent());
    }

    @Test
    void concurrentWritesReceiveUniqueIds() throws InterruptedException {
        int count = 200;
        ExecutorService executor = Executors.newFixedThreadPool(12);
        for (int i = 0; i < count; i++) {
            int value = i;
            executor.submit(() -> service.addComment(10, request("u" + value, "c" + value)));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        List<Integer> ids = new ArrayList<>();
        ids.addAll(service.getComments(10, 0, 100).content().stream()
                .map(CommentResponse::getId).toList());
        ids.addAll(service.getComments(10, 1, 100).content().stream()
                .map(CommentResponse::getId).toList());
        assertEquals(count, ids.size());
        assertEquals(count, ids.stream().distinct().count());
    }

    @Test
    void commentsAndRepliesShareTheSameRateLimit() {
        String userId = "shared-limit-user";
        CommentResponse comment = service.addComment(10, request(userId, "comment"));

        for (int i = 0; i < 4; i++) {
            service.addReply(10, comment.getId(), request(userId, "reply " + i));
        }

        assertThrows(RateLimitExceededException.class,
                () -> service.addReply(
                        10,
                        comment.getId(),
                        request(userId, "blocked reply")
                ));
        assertEquals(4, service.getReplies(comment.getId(), 0, 20).totalElements());
    }

    private static CreateCommentRequest request(String userId, String content) {
        return new CreateCommentRequest(userId, content);
    }
}
