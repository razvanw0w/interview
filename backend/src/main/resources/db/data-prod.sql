INSERT INTO application_users (id, username, password, role, enabled)
VALUES (1, 'admin', '$2a$10$cbFSpyLja.8yYxMpejHbcuPEpG2aCotrehX1tUKDGVAJ5CXlba1WO', 'ADMIN', TRUE);

INSERT INTO application_users (id, username, password, role, enabled)
VALUES (2, 'user', '$2a$10$/vsoUXUqawZzZ0coqKGsOe8JGIoabPeilM9KvRCGtgHEz6bKsxCVi', 'USER', TRUE);

INSERT INTO authors (name, email, created_at, updated_at)
VALUES ('PROD Joshua Bloch', 'joshua.bloch@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO authors (name, email, created_at, updated_at)
VALUES ('PROD Robert C. Martin', 'robert.martin@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO authors (name, email, created_at, updated_at)
VALUES ('PROD Martin Fowler', 'martin.fowler@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('PROD Effective Java', '9780134685991', 2018, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('PROD Java Puzzlers', '9780321336781', 2005, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('PROD Clean Code', '9780132350884', 2008, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('PROD Clean Architecture', '9780134494166', 2017, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO books (title, isbn, published_year, author_id, created_at, updated_at)
VALUES ('PROD Refactoring', '9780134757599', 2018, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);