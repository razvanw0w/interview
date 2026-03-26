docs/README.docker.md

# Docker guide

This document explains how to run the project as a multi-container local stack.

## What runs in Docker

The Compose setup includes:

- application
- Redis
- Prometheus
- Grafana
- k6 for ad hoc load testing

## Start the full stack

From the `backend` folder:

```bash
docker compose up --build
```

This will build the Spring Boot image and start the supporting services.

## Services and ports

- API: `http://localhost:8080/api`
- Redis: `localhost:6379`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

## Swagger and docs

- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/api/v3/api-docs`

## Grafana credentials

Default local credentials:

- username: `admin`
- password: `admin`

## Container roles

### app

Runs the Spring Boot API.

### redis

Stores cache entries for `getById` operations.

### prometheus

Scrapes application metrics.

### grafana

Provides dashboards over Prometheus data.

### k6

Runs smoke and load tests on demand.

## Common commands

Start the stack:

```bash
docker compose up --build
```

Start in detached mode:

```bash
docker compose up -d --build
```

Stop everything:

```bash
docker compose down
```

Show running containers:

```bash
docker compose ps
```

Stream logs:

```bash
docker compose logs -f
```

View app logs only:

```bash
docker compose logs -f app
```

## Rebuilding after code changes

When Java code, resources, or Docker-related files change:

```bash
docker compose up --build
```

If you want a cleaner restart:

```bash
docker compose down
docker compose up --build
```

## Redis inspection

To inspect keys in Redis:

```bash
docker exec -it interview-redis redis-cli
```

Examples:

```redis
KEYS *
GET bookById::1
TTL bookById::1
```

## Prometheus and Grafana

Prometheus scrapes the application metrics endpoint.

Grafana is provisioned with:

- a Prometheus data source
- dashboard definitions mounted from the repository

## Notes for future cloud deployment

This Docker-first structure is a good base for deploying later to container platforms such as AWS or Azure because:

- the app is already containerized
- config is externalized
- infrastructure concerns are separated from application code