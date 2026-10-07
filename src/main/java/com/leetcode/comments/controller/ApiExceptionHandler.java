package com.leetcode.comments.controller;
import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.exception.RateLimitExceededException;
import com.leetcode.comments.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(NotFoundException e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage()));}
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,String>> handleValidation(ValidationException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String,String>> handleRateLimit(RateLimitExceededException e){return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error",e.getMessage()));}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("request body is invalid");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableRequest(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "request body must contain valid field types"));
    }
}
