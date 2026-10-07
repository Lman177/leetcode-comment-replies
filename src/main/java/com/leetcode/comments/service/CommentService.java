package com.leetcode.comments.service;
import com.leetcode.comments.exception.NotFoundException;
import com.leetcode.comments.exception.ValidationException;
import com.leetcode.comments.model.CommentResponse;
import com.leetcode.comments.model.ReplyResponse;
import com.leetcode.comments.store.PostStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostStore postStore;
    private final Lock lock=new ReentrantLock();
    private final Map<Integer,LinkedHashMap<Integer,StoredComment>> commentsByPost=new HashMap<>();
    private final Map<Integer,StoredComment> commentsById=new HashMap<>();
    private int nextCommentId=1;
    private int nextReplyId=1;
    public CommentResponse addComment(int postId,String userId,String content){
        validateText(userId,"user_id"); validateText(content,"content");
        lock.lock();
        try {
            requirePost(postId);
            StoredComment comment=new StoredComment(nextCommentId++,postId,userId,content,nowSeconds());
            commentsByPost.computeIfAbsent(postId,ignored->new LinkedHashMap<>()).put(comment.id,comment);
            commentsById.put(comment.id,comment);
            return copyComment(comment);
        } finally {lock.unlock();}
    }
    public ReplyResponse addReply(int postId,int commentId,String userId,String content){
        validateText(userId,"user_id"); validateText(content,"content");
        lock.lock();
        try {
            requirePost(postId);
            StoredComment comment=commentsById.get(commentId);
            if(comment==null||comment.postId!=postId) throw new NotFoundException("Comment "+commentId+" was not found for post "+postId);
            StoredReply reply=new StoredReply(nextReplyId++,postId,commentId,userId,content,nowSeconds());
            comment.replies.add(reply);
            return copyReply(reply);
        } finally {lock.unlock();}
    }
    public List<CommentResponse> getComments(int postId){
        lock.lock();
        try {
            requirePost(postId);
            Map<Integer,StoredComment> comments=commentsByPost.get(postId);
            if(comments==null) return new ArrayList<>();
            return new ArrayList<>(comments.values().stream().map(this::copyComment).toList());
        } finally {lock.unlock();}
    }
    private void requirePost(int postId){if(!postStore.exists(postId)) throw new NotFoundException("Post "+postId+" was not found");}
    private static void validateText(String value,String field){if(value==null||value.trim().isEmpty()) throw new ValidationException(field+" must be a non-blank string");}
    private CommentResponse copyComment(StoredComment c){return new CommentResponse(c.id,c.postId,c.userId,c.content,c.createdAt,new ArrayList<>(c.replies.stream().map(this::copyReply).toList()));}
    private ReplyResponse copyReply(StoredReply r){return new ReplyResponse(r.id,r.postId,r.commentId,r.userId,r.content,r.createdAt);}
    private static double nowSeconds(){return Instant.now().toEpochMilli()/1000.0;}
    private static final class StoredComment {
        private final int id,postId; private final String userId,content; private final double createdAt; private final List<StoredReply> replies=new ArrayList<>();
        private StoredComment(int id,int postId,String userId,String content,double createdAt){this.id=id;this.postId=postId;this.userId=userId;this.content=content;this.createdAt=createdAt;}
    }
    private record StoredReply(int id,int postId,int commentId,String userId,String content,double createdAt){}
}
