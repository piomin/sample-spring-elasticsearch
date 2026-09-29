## Elasticsearch with Spring Boot  [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

[![CircleCI](https://circleci.com/gh/piomin/sample-spring-elasticsearch.svg?style=svg)](https://circleci.com/gh/piomin/sample-spring-elasticsearch)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-elasticsearch)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-elasticsearch&metric=bugs)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-elasticsearch)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-elasticsearch&metric=coverage)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-elasticsearch)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-spring-elasticsearch&metric=ncloc)](https://sonarcloud.io/dashboard?id=piomin_sample-spring-elasticsearch)

1. Detailed description for a standard option can be found here: [Elasticsearch with Spring Boot](https://piotrminkowski.com/2019/03/29/elasticsearch-with-spring-boot/)
2. Detailed description for a reactive option can be found here: [Reactive Elasticsearch with Spring Boot](https://piotrminkowski.wordpress.com/2019/10/25/reactive-elasticsearch-with-spring-boot/) 

---

## Overview

This repository is a reference/teaching project demonstrating two complementary approaches to integrating **Spring Boot** with **Elasticsearch**. Both approaches expose a REST API for managing `Employee` data stored in an Elasticsearch index, differing only in whether they use blocking or reactive I/O.

The project is a **Maven multi-module** application containing two modules:

| Module | Stack | Description |
|---|---|---|
| `employee-service` | Spring MVC + blocking Elasticsearch | Classical, imperative REST service |
| `employee-reactive-service` | Spring WebFlux + reactive Elasticsearch | Reactive, non-blocking REST service |

---

## Project Structure

```
sample-spring-elasticsearch/
├── pom.xml                            # Parent POM (Spring Boot 2.7.18, Java 21)
├── employee-service/                  # Blocking / imperative REST service
│   └── src/main/java/pl/piomin/services/elasticsearch/
│       ├── SampleApplication.java
│       ├── SampleDataSet.java         # Optional bulk data seeder
│       ├── controller/EmployeeController.java
│       ├── model/Employee.java
│       ├── model/Department.java
│       ├── model/Organization.java
│       └── repository/EmployeeRepository.java
└── employee-reactive-service/         # Reactive / non-blocking REST service
    └── src/main/java/pl/piomin/services/elasticsearch/
        ├── SampleApplication.java
        ├── SampleDataSet.java         # Optional reactive bulk data seeder
        ├── controller/EmployeeController.java
        ├── model/Employee.java
        ├── model/Department.java
        ├── model/Organization.java
        └── repository/EmployeeRepository.java
```

---

## Technologies

- **Java 21**
- **Spring Boot 2.7.18**
- **Spring Data Elasticsearch** — blocking (`ElasticsearchRepository`) and reactive (`ReactiveCrudRepository`)
- **Spring MVC** (`employee-service`) — servlet-based REST with embedded Tomcat
- **Spring WebFlux** (`employee-reactive-service`) — reactive REST with embedded Netty and Project Reactor (`Flux`/`Mono`)
- **Spring Boot Actuator** — health and metrics endpoints
- **Testcontainers** — integration tests against a real Elasticsearch node (no external setup needed)
- **JUnit 5** (`employee-service`) and **JUnit 4** (`employee-reactive-service`)

---

## Domain Model

Both modules share the same domain model. `Employee` is the root Elasticsearch document (stored in the `employees` index), with `Department` and `Organization` embedded as nested objects.

```
Employee (@Document indexName="employees")
├── id         : String   (@Id)
├── name       : String
├── age        : int
├── position   : String
├── department : Department
│   ├── id   : Long
│   └── name : String
└── organization : Organization
    ├── id      : Long
    ├── name    : String
    └── address : String
```

---

## REST API

### `employee-service` (blocking, Spring MVC)

| Method | Path | Description |
|---|---|---|
| `POST` | `/employees` | Save a new employee |
| `GET` | `/employees/{name}` | Find employees by name |
| `GET` | `/employees/organization/{organizationName}` | Find employees by organization name |

### `employee-reactive-service` (reactive, Spring WebFlux)

| Method | Path | Description |
|---|---|---|
| `POST` | `/employees` | Save a new employee (returns `Mono<Employee>`) |
| `GET` | `/employees` | Retrieve all employees (returns `Flux<Employee>`) |
| `GET` | `/employees/{name}` | Find employees by name (returns `Flux<Employee>`) |
| `GET` | `/employees/count/all` | Count all documents in the index |
| `GET` | `/employees/organization/{organizationName}` | Find employees by organization name |
| `POST` | `/employees/generate` | Generate and save 200 random employees |

---

## Repository Layer

### Blocking (`employee-service`)

```java
public interface EmployeeRepository extends ElasticsearchRepository<Employee, Long> {
    List<Employee> findByOrganizationName(String name);
    List<Employee> findByName(String name);
}
```

### Reactive (`employee-reactive-service`)

```java
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {
    Flux<Employee> findByOrganizationName(String name);
    Flux<Employee> findByName(String name);
}
```

Both repositories use Spring Data's query derivation from method names — no custom queries required.

---

## Configuration

By default, both services connect to Elasticsearch at `http://192.168.99.100:9200` (a Docker Machine default gateway). To override this, update `application.yml` in the respective module:

**`employee-service`**
```yaml
spring:
  elasticsearch:
    rest:
      uris: http://<your-es-host>:9200
```

**`employee-reactive-service`**
```yaml
spring:
  data:
    elasticsearch:
      client:
        reactive:
          endpoints: <your-es-host>:9200
```

To enable bulk data seeding on startup, set:
```yaml
initial-import:
  enabled: true
```

---

## Running Tests

Tests use **Testcontainers** to automatically start a real Elasticsearch Docker container — no local Elasticsearch installation is needed. Ensure Docker is running, then:

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl employee-service
mvn test -pl employee-reactive-service
```

The `employee-reactive-service` module also includes `EmployeeRepositoryPerformanceTest`, a benchmark that runs 500 rounds across 30 concurrent threads against a locally running service on port `8080`.

---

## Building

```bash
# Build all modules
mvn clean install

# Skip tests
mvn clean install -DskipTests
```