# Comments and Replies — Spring Boot Practice

## Goal

Build a database-backed discussion API with a two-level hierarchy:

```text
Post -> Comment -> Reply
```

Posts, comments, and replies are durable application data and must be persisted
through Spring Data JPA. In-memory state is reserved for a per-user write rate
limiter, where short-lived process-local data is appropriate.

## Architecture

```text
HTTP controller
      |
CommentService interface / CommentServiceImpl (@Transactional)
      |-------------------------------|
      |                               |
Spring Data JPA                 DiscussionRateLimiter
      |                         (in-memory sliding window)
H2 database
```

- `Post`, `Comment`, and `Reply` are JPA entities.
- `PostRepository`, `CommentRepository`, and `ReplyRepository` provide persistence.
- Response DTOs keep persistence entities out of the API contract.
- H2 is used for local development and is recreated when the application restarts.
- Controllers depend on service interfaces rather than concrete implementations.
- `InMemoryDiscussionRateLimiter` stores only temporary request timestamps in RAM.

## Seed data

`data.sql` creates the initial posts:

- `10`: `Welcome to LeetCode`
- `20`: `System Design Guide`

Any operation referencing a missing post must return HTTP `404`.

## Functional requirements

The application must:

- Add a comment to an existing post.
- Add a reply to an existing comment under the specified post.
- Persist comments and replies in H2 through JPA repositories.
- Prevent replies from being added through a post that does not own the comment.
- Return comments and replies in creation order.
- Support exactly two discussion levels; replies cannot contain replies.
- Return DTOs matching the JSON contracts below.
- Execute write operations transactionally.
- Apply the in-memory write rate limit before persisting a comment or reply.

## Validation order

For both write operations:

1. Validate `user_id`.
2. Validate `content`.
3. Apply the write rate limit.
4. Verify that the post exists.
5. For a reply, verify that the comment exists under the specified post.

`user_id` and `content` must be JSON strings containing at least one
non-whitespace character. Invalid values return HTTP `400`.

## API contract

### Health

```http
GET /health
```

```json
{"status": "ok"}
```

### Add comment

```http
POST /post/{postId}/comment/add
Content-Type: application/json
```

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

### Add reply

```http
POST /post/{postId}/comment/{commentId}/reply
Content-Type: application/json
```

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

### Get discussion

```http
GET /post/{postId}/comments
```

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

An existing post without comments returns `[]`.

## Error responses

- HTTP `400`: invalid `user_id` or `content`.
- HTTP `404`: missing post, missing comment, or comment belongs to another post.
- HTTP `429`: the user exceeded the write rate limit.

Error body:

```json
{"error": "description"}
```

## Database requirements

- `Post`, `Comment`, and `Reply` use separate database tables.
- Comments reference posts with a required foreign key.
- Replies reference comments with a required foreign key.
- Comment and reply IDs are generated independently by their tables.
- `created_at` is stored as a database column.
- Query ordering must be explicit and must not depend on collection implementation details.
- Service write methods use `@Transactional`.
- Service read methods use `@Transactional(readOnly = true)`.
- Entities must be mapped to response DTOs before being returned by controllers.

## In-memory rate limiter requirements

`DiscussionRateLimiter` and `InMemoryDiscussionRateLimiter` form the separate
RAM-based exercise.

- A user may create at most **5 comments or replies in any rolling 60-second window**.
- Comments and replies share the same limit for a user.
- Store recent accepted write-attempt timestamps per `user_id`.
- Remove expired timestamps before evaluating the current request.
- The implementation must be safe under concurrent requests.
- Rejected requests must throw `RateLimitExceededException` and return HTTP `429`.
- A rejected request must not be persisted.
- Rate-limit state may reset when the application restarts.
- Document that production horizontal scaling would require shared state such as Redis.

## TODO scope

Implement:

- `CommentServiceImpl.addComment`.
- `CommentServiceImpl.addReply`.
- `CommentServiceImpl.getComments`.
- Entity-to-DTO mapping and input validation inside the service.
- `InMemoryDiscussionRateLimiter` using a sliding window and concurrency control.
- The design discussion in `REFLECTION.essay`.

The entity mappings, repositories, controller, exception handler, H2 configuration,
seed data, DTOs, and tests are provided as starter infrastructure.

## Technology

- Java 17+
- Maven 3.9+
- Spring Boot
- Spring Web
- Spring Data JPA
- H2
- Lombok
- JUnit 5

## Run

```bash
mvn spring-boot:run
```

The API listens on `http://localhost:8080`.

H2 console: `http://localhost:8080/h2-console`

```text
JDBC URL: jdbc:h2:mem:commentsdb
Username: sa
Password: (empty)
```

## Test

The service and rate-limiter tests will fail until their TODOs are implemented.
To verify only the JPA setup and seed data:

```bash
mvn -Dtest=PostRepositoryTest test
```

After completing the TODOs:

```bash
mvn test
```
