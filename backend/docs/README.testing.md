docs/README.testing.md

# Testing guide

This document describes the automated testing layers in the project.

## Test types included

The project includes:

- unit tests
- integration tests
- k6 load tests

## Unit and integration tests

Run the Java test suite from the `backend` folder:

\`\`\`bash
mvn test
\`\`\`

## Integration test behavior

Integration tests are designed to exercise the API through HTTP and verify realistic application behavior.

They cover areas such as:

- authentication
- CRUD operations
- pagination and sorting
- error responses
- authorization behavior

## Test profile

The tests run under the `test` Spring profile.

This allows separation of:

- test data
- test JWT keys
- test-oriented application settings

## Test data reset

Integration tests reset the data set before test methods so tests are isolated and reproducible.

This avoids state leaking from one test into another.

## Why this matters

The combination of unit and integration tests demonstrates:

- isolated correctness for service logic
- realistic end-to-end verification at the HTTP layer
- stable regression coverage as features evolve

## Recommended workflow

During active development:

1. run `mvn test`
2. inspect failures locally
3. rerun after code changes
4. optionally validate behavior through Swagger or curl
5. use k6 for performance-oriented validation once functionality is stable

## Additional manual checks

Useful manual validations include:

- login flow
- secured endpoint access with bearer token
- cache-backed `getById` operations
- metrics exposure through Actuator

## Future extension ideas

The test strategy could be expanded with:

- Redis-enabled integration tests
- containerized test dependencies
- contract testing
- CI pipeline execution with environment-specific stages