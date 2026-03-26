README.md

# Authors / Books API

A Spring Boot REST API for managing authors and books, built to showcase secure CRUD design, pageable endpoints, Redis
caching, containerization, observability, and load testing.

This project goes beyond a basic CRUD exercise by including:

- JWT-based authentication
- role-based authorization with `USER` and `ADMIN`
- pagination and sorting on list endpoints
- Redis caching for `getById` operations
- Docker Compose orchestration
- Prometheus metrics and Grafana dashboards
- integration, unit, and k6 load tests

## Main capabilities

- Manage authors and books through REST endpoints
- Authenticate with `/api/login` and call protected APIs with bearer tokens
- Read paginated and sorted collections
- Cache hot `getById` lookups in Redis
- Inspect health and metrics through Actuator
- Visualize metrics with Prometheus and Grafana
- Exercise the API under load with k6

## Technical stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- H2
- Redis
- Docker / Docker Compose
- Prometheus
- Grafana
- k6
- springdoc OpenAPI

## Project structure

- `backend/` — Spring Boot application
- `observability/` — Prometheus and Grafana provisioning
- `testing/k6/` — load test scripts
- `testing/results/` — result screenshots and test artifacts
- `docs/` — supporting project documentation

## Documentation

- [Run locally](docs/README.local.md)
- [API guide](docs/README.api.md)
- [Docker guide](docs/README.docker.md)
- [Observability guide](docs/README.observability.md)
- [Testing guide](docs/README.testing.md)
- [Load testing guide](docs/README.load-testing.md)

## Quick start

Start the full stack:

```bash
docker compose up --build
```

Important URLs:

- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/api/v3/api-docs`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

## Motivation

This codebase demonstrates how a small Spring Boot API can evolve into a more production-minded service by adding:

- secure authentication and authorization
- consistent layering and separation of concerns
- infrastructure through containers
- cache-backed optimizations
- metrics and dashboards
- repeatable load testing

## Project location

The Spring Boot application root is in the `backend` folder.