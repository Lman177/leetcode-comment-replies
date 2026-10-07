package com.leetcode.comments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CommentsRepliesApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommentsRepliesApplication.class, args);
    }
}
