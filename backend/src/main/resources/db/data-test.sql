INSERT INTO authors (name, email, created_at, updated_at)
VALUES ('Joshua Bloch', 'joshua.bloch@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO authors (name, email, created_at, updated_at)
VALUES ('Robert C. Martin', 'robert.martin@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO authors (name, email, created_at, updated_at)
VALUES ('Martin Fowler', 'martin.fowler@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('Effective Java', '9780134685991', 2018, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('Java Puzzlers', '9780321336781', 2005, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('Clean Code', '9780132350884', 2008, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('Clean Architecture', '9780134494166', 2017, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('Refactoring', '9780134757599', 2018, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);