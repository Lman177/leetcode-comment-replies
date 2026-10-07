package com.leetcode.comments.service;

import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.exception.ValidationException;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.repository.CommentRepository;
import com.leetcode.comments.repository.ReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        CommentResponse first = service.addComment(10, "u1", "First");
        CommentResponse second = service.addComment(10, "u2", "Second");
        ReplyResponse reply = service.addReply(10, first.getId(), "u3", "Reply");

        List<CommentResponse> thread = service.getComments(10);
        assertEquals(List.of(first.getId(), second.getId()),
                thread.stream().map(CommentResponse::getId).toList());
        assertEquals(1, reply.getId());
        assertEquals(1, thread.get(0).getReplies().size());
    }

    @Test
    void isolatesPostsAndRejectsReplyThroughWrongPost() {
        CommentResponse comment = service.addComment(10, "u1", "hello");
        assertEquals(0, service.getComments(20).size());
        assertThrows(NotFoundException.class,
                () -> service.addReply(20, comment.getId(), "u2", "wrong post"));
    }

    @Test
    void validatesTextBeforeLookingUpPost() {
        assertThrows(ValidationException.class,
                () -> service.addComment(999, " ", "content"));
        assertThrows(ValidationException.class,
                () -> service.addReply(999, 123, "user", "\t"));
    }

    @Test
    void returnedObjectsCannotMutateStoredState() {
        CommentResponse created = service.addComment(10, "u1", "original");
        service.addReply(10, created.getId(), "u2", "original reply");

        List<CommentResponse> result = service.getComments(10);
        result.get(0).setContent("changed");
        result.get(0).getReplies().get(0).setContent("changed reply");
        result.clear();

        List<CommentResponse> fresh = service.getComments(10);
        assertEquals("original", fresh.get(0).getContent());
        assertEquals("original reply", fresh.get(0).getReplies().get(0).getContent());
    }

    @Test
    void concurrentWritesReceiveUniqueIds() throws InterruptedException {
        int count = 200;
        ExecutorService executor = Executors.newFixedThreadPool(12);
        for (int i = 0; i < count; i++) {
            int value = i;
            executor.submit(() -> service.addComment(10, "u" + value, "c" + value));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        List<Integer> ids = service.getComments(10).stream().map(CommentResponse::getId).toList();
        assertEquals(count, ids.size());
        assertEquals(count, ids.stream().distinct().count());
    }
}
