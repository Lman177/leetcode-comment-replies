package com.leetcode.comments.controller;

import com.leetcode.comments.service.CommentService;
import com.leetcode.comments.store.InMemoryPostStore;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {
    @Test
    void exposesSnakeCaseContract() throws Exception {
        CommentService service = new CommentService(new InMemoryPostStore());
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new CommentController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();

        mockMvc.perform(post("/post/10/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"u1\",\"content\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.post_id").value(10))
                .andExpect(jsonPath("$.user_id").value("u1"))
                .andExpect(jsonPath("$.created_at").isNumber())
                .andExpect(jsonPath("$.replies").isArray());

        mockMvc.perform(post("/post/999/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":123,\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest());
    }
}
