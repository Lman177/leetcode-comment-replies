# Q2: In-memory Comments and Replies — Java

## Scenario

The discussion platform already supports posts. This project adds comments and
replies using an in-memory Spring Boot service.

The discussion hierarchy has exactly two levels:

```text
Post -> Comment -> Reply
```

A reply belongs directly to a comment. Deeper reply nesting is not supported.

## Functional requirements

The service must:

- Add comments to existing posts.
- Add replies to existing comments.
- Use independent auto-incrementing integer ID sequences for comments and replies.
- Return JSON matching the contracts below exactly.
- Prevent callers from mutating internal state through returned objects.
- Be safe under concurrent requests.
- Keep comments and replies isolated between posts.

## Available posts

The in-memory post store contains:

- `10`: `Welcome to LeetCode`
- `20`: `System Design Guide`

Any operation referencing a missing `post_id` must fail with HTTP `404`.

## Validation requirements

For both write operations, `user_id` and `content` must be JSON strings containing
at least one non-whitespace character. Reject null, non-string values, empty strings,
and whitespace-only strings with HTTP `400`.

Validation order is significant:

1. Validate `user_id`.
2. Validate `content`.
3. Check that the post exists.
4. For replies, check that the comment exists under the specified post.

## API contract

### Health check

```http
GET /health
```

Response:

```json
{
  "status": "ok"
}
```

### Add comment

```http
POST /post/{postId}/comment/add
Content-Type: application/json
```

Request:

```json
{
  "user_id": "u1",
  "content": "Hello Leet"
}
```

Successful response: HTTP `201`

```json
{
  "id": 1,
  "post_id": 10,
  "user_id": "u1",
  "content": "Hello Leet",
  "created_at": 1781086734.0,
  "replies": []
}
```

Errors:

- HTTP `400` when `user_id` or `content` is invalid.
- HTTP `404` when the post does not exist.

### Add reply

```http
POST /post/{postId}/comment/{commentId}/reply
Content-Type: application/json
```

Request:

```json
{
  "user_id": "u2",
  "content": "Hi Code"
}
```

Successful response: HTTP `201`

```json
{
  "id": 1,
  "post_id": 10,
  "comment_id": 1,
  "user_id": "u2",
  "content": "Hi Code",
  "created_at": 1781086739.0
}
```

Errors:

- HTTP `400` when `user_id` or `content` is invalid.
- HTTP `404` when the post does not exist.
- HTTP `404` when the comment does not exist or belongs to another post.

### Get comments

```http
GET /post/{postId}/comments
```

Successful response: HTTP `200`

```json
[
  {
    "id": 1,
    "post_id": 10,
    "user_id": "u1",
    "content": "Hello Leet",
    "created_at": 1781086734.0,
    "replies": [
      {
        "id": 1,
        "post_id": 10,
        "comment_id": 1,
        "user_id": "u2",
        "content": "Hi Code",
        "created_at": 1781086739.0
      }
    ]
  }
]
```

An existing post with no comments returns `[]`. A missing post returns HTTP `404`.

## Expected semantics

### IDs and ownership

- A comment belongs to exactly one post.
- A reply belongs to exactly one comment.
- Comment and reply IDs use separate global sequences.
- The first comment ID and first reply ID are both `1`.
- A comment cannot be accessed through a different post.

### Ordering

- Comments are returned in successful commit order.
- Replies are returned in successful commit order.
- `created_at` is response metadata and is not the source of truth for ordering.
- `created_at` is represented in seconds.

### Mutation safety

Responses must be detached copies of internal state. Modifying a returned list,
comment, or reply must not affect future results from the service.

### Concurrency

Requests may execute concurrently. IDs must remain unique, writes must not corrupt
one another, and activity on one post must not leak into another. A single lock is
sufficient for this exercise.

### Complexity

- Adding a comment should be efficient.
- Adding a reply should avoid a full-system scan.
- Retrieving a thread should be proportional to the comments and replies returned.

## Technical requirements

- Java 17 or newer.
- Maven 3.9 or newer.
- Spring Boot.
- Lombok annotation processing enabled in the IDE.

## Run and test

Run all tests:

```bash
mvn test
```

Start the server:

```bash
mvn spring-boot:run
```

The API listens on `http://localhost:8080`.

Build an executable JAR:

```bash
mvn clean package
java -jar target/comments-replies-1.0.0.jar
```
