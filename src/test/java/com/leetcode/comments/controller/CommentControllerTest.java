package com.leetcode.comments.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesSnakeCaseContract() throws Exception {
        mockMvc.perform(post("/post/10/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"u1\",\"content\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.post_id").value(10))
                .andExpect(jsonPath("$.user_id").value("u1"))
                .andExpect(jsonPath("$.created_at").isString());

        mockMvc.perform(post("/post/999/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":123,\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/post/999/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":\"  \",\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("user_id must be a non-blank string"));
    }

    @Test
    void exposesCommentsAndRepliesThroughSeparateEndpoints() throws Exception {
        mockMvc.perform(get("/posts/10/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(101))
                .andExpect(jsonPath("$.content[0].post_id").value(10))
                .andExpect(jsonPath("$.content[0].replies").doesNotExist())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.total_elements").isNumber());

        mockMvc.perform(get("/comments/101/replies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1001))
                .andExpect(jsonPath("$.content[0].comment_id").value(101))
                .andExpect(jsonPath("$.total_elements").value(2));

        mockMvc.perform(get("/comments/999999/replies"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/posts/10/comments?page=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("page must be greater than or equal to 0"));
    }
}
