package com.leetcode.comments.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
