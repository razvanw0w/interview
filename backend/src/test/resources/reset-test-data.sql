DELETE FROM books;
DELETE FROM authors;
DELETE from application_users

INSERT INTO application_users (id, username, password, role, enabled)
VALUES (1, 'admin', '$2a$10$7of0o4U5CFQLS9epJHSNd.Q81UHpu/rxZFLyeYnXDb9/b0cysgVaC', 'ADMIN', TRUE);

INSERT INTO application_users (id, username, password, role, enabled)
VALUES (2, 'user', '$2a$10$R1hUh3ODwl5dUeRR2cmv4ugiL7MMWf5TI/gYmM6IPiXFDa9PcXhm6', 'USER', TRUE);

INSERT INTO authors (id, name, email, created_at, updated_at)
VALUES (1, 'Joshua Bloch', 'joshua.bloch@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO authors (id, name, email, created_at, updated_at)
VALUES (2, 'Robert C. Martin', 'robert.martin@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO authors (id, name, email, created_at, updated_at)
VALUES (3, 'Martin Fowler', 'martin.fowler@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (id, title, isbn, published_year, author_id, created_at, updated_at)
VALUES (1, 'Effective Java', '9780134685991', 2018, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (id, title, isbn, published_year, author_id, created_at, updated_at)
VALUES (2, 'Java Puzzlers', '9780321336781', 2005, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (id, title, isbn, published_year, author_id, created_at, updated_at)
VALUES (3, 'Clean Code', '9780132350884', 2008, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (id, title, isbn, published_year, author_id, created_at, updated_at)
VALUES (4, 'Clean Architecture', '9780134494166', 2017, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (id, title, isbn, published_year, author_id, created_at, updated_at)
VALUES (5, 'Refactoring', '9780134757599', 2018, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE authors ALTER COLUMN id RESTART WITH 4;
ALTER TABLE books ALTER COLUMN id RESTART WITH 6;