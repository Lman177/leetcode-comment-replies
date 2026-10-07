package com.leetcode.comments.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.defer-datasource-initialization=true",
        "spring.sql.init.mode=always"
})
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    void loadsSeedPosts() {
        assertEquals(2, postRepository.count());
        assertTrue(postRepository.existsById(10));
        assertTrue(postRepository.existsById(20));
    }
}
