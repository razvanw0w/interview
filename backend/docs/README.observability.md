docs/README.observability.md

# Observability guide

This document explains how metrics and dashboards are wired into the project.

## Overview

The application exposes operational telemetry through:

- Spring Boot Actuator
- Micrometer
- Prometheus
- Grafana

This gives visibility into:

- application health
- HTTP request activity
- JVM behavior
- runtime performance under load

## Health endpoint

Health is exposed through:

- `http://localhost:8080/api/actuator/health`

This is useful for:

- local diagnostics
- readiness checks
- infrastructure health probing

## Prometheus metrics endpoint

Prometheus scrapes metrics from:

- `http://localhost:8080/api/actuator/prometheus`

This endpoint exposes metrics in Prometheus format.

## Prometheus

Prometheus runs locally at:

- `http://localhost:9090`

It is configured to scrape the Spring Boot application over the Compose network.

Typical metrics of interest include:

- HTTP request duration and count
- JVM memory
- process uptime
- thread and GC metrics

## Grafana

Grafana runs locally at:

- `http://localhost:3000`

Default local credentials:

- username: `admin`
- password: `admin`

Grafana is provisioned automatically with:

- Prometheus as a data source
- dashboard definitions from the repository

## Provisioning

The project stores Grafana provisioning under:

- `observability/grafana/provisioning/`
- `observability/grafana/dashboards/`

This makes dashboards reproducible and version-controlled.

## What can be observed

With the current setup, you can inspect:

- application liveness and health
- request throughput
- latency under load
- JVM memory behavior
- system response during cache-heavy access patterns

## Typical observability workflow

1. Start the stack with Docker Compose
2. Open Prometheus to verify target scraping
3. Open Grafana and inspect the provisioned dashboards
4. Run k6 smoke or load tests
5. Watch latency, throughput, and resource patterns evolve in real time

## Prometheus queries worth exploring

Some useful starting points are:

- `http_server_requests_seconds_count`
- `process_uptime_seconds`
- `jvm_memory_used_bytes`

## Why this matters

This observability setup helps demonstrate that the project is not just functionally correct, but also measurable and
inspectable under realistic runtime conditions.