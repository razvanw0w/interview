docs/README.local.md

# Run locally

This document explains how to run the project without Docker for day-to-day development.

## Prerequisites

- Java 21
- Maven

## Project root

The Spring Boot application lives in:

- `backend/`

All commands below should be run from the `backend` folder unless stated otherwise.

## Run with the test profile

For local development, the simplest option is to start the app with the `test` profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

You can also package and run the jar directly:

```bash
mvn clean package
java -jar target/interview-1.0-SNAPSHOT.jar --spring.profiles.active=test
```

## Profiles

The project uses separate Spring profiles to separate environment-specific configuration:

- `test`
- `prod`

These profiles control things like:

- datasource settings
- seed data
- JWT key locations
- environment-specific operational settings

## Local database

The project uses an in-memory H2 database.

Typical local test settings:

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

Because the database is in memory, data resets on application restart.

## Local URLs

Once the app is running, useful local endpoints are:

- API root: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/api/v3/api-docs`
- Health: `http://localhost:8080/api/actuator/health`

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

The response returns an access token that can be used for protected endpoints.

## Protected request example

```bash
curl --location 'http://localhost:8080/api/books/1' \
--header 'Authorization: Bearer <access-token>'
```

## Common local development workflow

1. Start the app with the `test` profile
2. Open Swagger UI to inspect endpoints
3. Authenticate through `/api/login`
4. Use the returned bearer token for protected routes
5. Run tests with `mvn test`

## Troubleshooting

### Port already in use

If port `8080` is already taken, stop the conflicting process or run the app with a different server port.

### Login fails

Check that:

- the app is running with the expected profile
- the seeded test users are loaded
- your JWT key files are available on the classpath

### Data resets after restart

This is expected with H2 in-memory mode.