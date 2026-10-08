INSERT INTO posts (id, title, created_at, updated_at)
VALUES (10, 'Welcome to LeetCode', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO posts (id, title, created_at, updated_at)
VALUES (20, 'System Design Guide', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO comments (id, post_id, user_id, content, created_at, updated_at)
VALUES (101, 10, 'alice', 'What is the time complexity of this solution?', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO comments (id, post_id, user_id, content, created_at, updated_at)
VALUES (102, 10, 'bob', 'The two-pointer approach is easier to follow.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO comments (id, post_id, user_id, content, created_at, updated_at)
VALUES (103, 20, 'charlie', 'How would this design scale across regions?', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO replies (id, comment_id, user_id, content, created_at, updated_at)
VALUES (1001, 101, 'diana', 'It is O(n) time and O(1) extra space.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO replies (id, comment_id, user_id, content, created_at, updated_at)
VALUES (1002, 101, 'alice', 'Thanks, that makes sense.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO replies (id, comment_id, user_id, content, created_at, updated_at)
VALUES (1003, 103, 'eric', 'Start by separating regional writes from global reads.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE comments ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE replies ALTER COLUMN id RESTART WITH 2000;
