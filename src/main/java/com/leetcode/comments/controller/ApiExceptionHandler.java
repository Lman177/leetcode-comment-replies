package com.leetcode.comments.controller;
import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(NotFoundException e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage()));}
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,String>> handleValidation(ValidationException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
}
