docs/README.api.md

# API guide

This document provides a concise overview of the domain, authentication flow, and endpoint usage.

## Base URL

All endpoints are exposed under the application context path:

- `http://localhost:8080/api`

## Domain model

### Author

Represents an author in the system.

Typical fields:

- `id`
- `name`
- `email`
- `books`

### Book

Represents a book associated with an author.

Typical fields:

- `id`
- `title`
- `isbn`
- `publishedYear`
- `authorId`
- `authorName`

## Authentication

Authentication is done through:

- `POST /api/login`

The login response returns a JWT access token.

Send that token with protected requests:

```http
Authorization: Bearer <access-token>
```

## Role-based authorization

Endpoints are protected through Spring Security and method-level authorization.

Roles include:

- `USER`
- `ADMIN`

## Swagger / OpenAPI

Interactive API documentation is available at:

- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/api/v3/api-docs`

## Login example

```bash
curl --location 'http://localhost:8080/api/login' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
"username": "user",
"password": "user123"
}'
```

## Endpoint overview

### Authors

- `POST /api/authors`
- `GET /api/authors`
- `GET /api/authors/{id}`
- `PUT /api/authors/{id}`
- `DELETE /api/authors/{id}`

### Books

- `POST /api/books`
- `GET /api/books`
- `GET /api/books/{id}`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

## Pagination and sorting

List endpoints support paging and sorting through query parameters such as:

- `page`
- `size`
- `sort`

Example:

```bash
curl --location 'http://localhost:8080/api/books?page=0&size=10&sort=id,asc' \
--header 'Authorization: Bearer <access-token>'
```

Another example:

```bash
curl --location 'http://localhost:8080/api/authors?page=0&size=5&sort=name,asc' \
--header 'Authorization: Bearer <access-token>'
```

## Example read requests

Get one book:

```bash
curl --location 'http://localhost:8080/api/books/1' \
--header 'Authorization: Bearer <access-token>'
```

Get one author:

```bash
curl --location 'http://localhost:8080/api/authors/1' \
--header 'Authorization: Bearer <access-token>'
```

## Example write requests

Create an author:

```bash
curl --location 'http://localhost:8080/api/authors' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <access-token>' \
--data '{
"name": "Kent Beck",
"email": "kent.beck@example.com"
}'
```

Create a book:

```bash
curl --location 'http://localhost:8080/api/books' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <access-token>' \
--data '{
"title": "Test-Driven Development",
"isbn": "9780321146533",
"publishedYear": 2002,
"authorId": 1
}'
```

Update a book:

```bash
curl --location --request PUT 'http://localhost:8080/api/books/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <access-token>' \
--data '{
"title": "Effective Java 3rd Edition",
"isbn": "9780134685991",
"publishedYear": 2018,
"authorId": 1
}'
```

Delete a book:

```bash
curl --location --request DELETE 'http://localhost:8080/api/books/1' \
--header 'Authorization: Bearer <access-token>'
```

## Operational notes

- List endpoints are pageable and sortable
- `getById` operations are cache-backed through Redis
- secured endpoints require a valid JWT
- health and documentation endpoints are intentionally easier to access for tooling and diagnostics