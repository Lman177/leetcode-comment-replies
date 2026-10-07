package com.leetcode.comments.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.leetcode.comments.exception.ValidationException;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.CreateRequest;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok");}
    @PostMapping("/post/{postId}/comment/add") @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable int postId,@RequestBody CreateRequest request){return commentService.addComment(postId,text(request.userId(),"user_id"),text(request.content(),"content"));}
    @PostMapping("/post/{postId}/comment/{commentId}/reply") @ResponseStatus(HttpStatus.CREATED)
    public ReplyResponse addReply(@PathVariable int postId,@PathVariable int commentId,@RequestBody CreateRequest request){return commentService.addReply(postId,commentId,text(request.userId(),"user_id"),text(request.content(),"content"));}
    @GetMapping("/post/{postId}/comments") public List<CommentResponse> getComments(@PathVariable int postId){return commentService.getComments(postId);}
    private static String text(JsonNode value,String field){
        if(value==null||!value.isTextual()) throw new ValidationException(field+" must be a non-blank string");
        return value.textValue();
    }
}
