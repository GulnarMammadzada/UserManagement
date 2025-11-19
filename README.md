# User Management Service

A comprehensive, production-ready RESTful API service for managing users, built with Spring Boot 3, PostgreSQL, Kafka, and Docker.

[![CI/CD Pipeline](https://github.com/GulnarMammadzada/UserManagement/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/GulnarMammadzada/UserManagement/actions)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running with Docker](#running-with-docker)
- [API Documentation](#api-documentation)
- [Example API Calls](#example-api-calls)
- [Database Schema](#database-schema)
- [Kafka Events](#kafka-events)
- [Testing](#testing)
- [Deployment](#deployment)
- [Configuration](#configuration)
- [Contributing](#contributing)

## Features

### Core Features
- ✅ Complete CRUD operations for user management
- ✅ Advanced filtering and search capabilities
- ✅ Pagination and sorting support
- ✅ Comprehensive validation with detailed error messages
- ✅ RESTful API design following best practices

### Advanced Features
- ✅ **Event-Driven Architecture** with Apache Kafka
- ✅ **Database Migrations** with Flyway
- ✅ **Comprehensive Testing** (Unit, Integration, Repository tests)
- ✅ **API Documentation** with OpenAPI/Swagger
- ✅ **Docker Support** with multi-container setup
- ✅ **CI/CD Pipeline** with GitHub Actions
- ✅ **Health Checks** and Actuator endpoints
- ✅ **Production-Ready** with proper logging and monitoring
- ✅ **Multi-Environment Configuration**

## Tech Stack

### Core
- **Java 17** - Programming Language
- **Spring Boot 3.5.7** - Application Framework
- **Spring Data JPA** - Data Access Layer
- **PostgreSQL 16** - Relational Database
- **Gradle** - Build Tool

### Additional Technologies
- **Apache Kafka** - Event Streaming Platform
- **Flyway** - Database Migration Tool
- **Lombok** - Boilerplate Code Reduction
- **SpringDoc OpenAPI** - API Documentation
- **Docker & Docker Compose** - Containerization
- **JUnit 5 & Mockito** - Testing Framework
- **Testcontainers** - Integration Testing

## Architecture

```
src/
├── main/
│   ├── java/com/example/usermanagement/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions & handlers
│   │   ├── kafka/           # Kafka producers/consumers
│   │   ├── repository/      # Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       ├── db/migration/    # Flyway migrations
│       └── application.properties
└── test/                    # Comprehensive test suite
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Docker & Docker Compose
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/GulnarMammadzada/UserManagement.git
cd UserManagement
```

2. **Create environment file**
```bash
cp .env.example .env
```

3. **Build the project**
```bash
./gradlew clean build
```

### Running with Docker

**Start all services (recommended):**
```bash
docker-compose up -d
```

This will start:
- PostgreSQL database (port 5432)
- Apache Kafka (port 9092)
- Zookeeper (port 2181)
- User Management Service (port 8080)

**Stop all services:**
```bash
docker-compose down
```

**View logs:**
```bash
docker-compose logs -f app
```

### Running Locally (without Docker)

1. **Start PostgreSQL and Kafka** (using Docker Compose for dependencies only):
```bash
docker-compose up -d postgres kafka zookeeper
```

2. **Run the application**:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Documentation

### Interactive API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Health Check

- **Health Endpoint**: http://localhost:8080/api/v1/health

```bash
curl http://localhost:8080/api/v1/health
```

Response:
```json
{
  "status": "UP",
  "timestamp": "2025-11-17T10:30:45",
  "service": "User Management Service",
  "version": "1.0.0"
}
```

## Example API Calls

### 1. Create a New User

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "address": "123 Main Street",
    "city": "New York",
    "country": "USA",
    "postalCode": "10001",
    "role": "USER",
    "status": "ACTIVE",
    "bio": "Software Engineer",
    "avatarUrl": "https://i.pravatar.cc/150"
  }'
```

Response (201 Created):
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "address": "123 Main Street",
  "city": "New York",
  "country": "USA",
  "postalCode": "10001",
  "role": "USER",
  "status": "ACTIVE",
  "bio": "Software Engineer",
  "avatarUrl": "https://i.pravatar.cc/150",
  "createdAt": "2025-11-17 10:30:45",
  "updatedAt": "2025-11-17 10:30:45",
  "lastLoginAt": null
}
```

### 2. Get User by ID

```bash
curl http://localhost:8080/api/v1/users/1
```

### 3. Get All Users (with Pagination)

```bash
curl "http://localhost:8080/api/v1/users?page=0&size=10&sortBy=id&sortDir=DESC"
```

Response:
```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  "empty": false
}
```

### 4. Update User

```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com",
    "phone": "+1234567890",
    "role": "MANAGER",
    "status": "ACTIVE"
  }'
```

### 5. Delete User

```bash
curl -X DELETE http://localhost:8080/api/v1/users/1
```

Response: 204 No Content

### 6. Search Users

```bash
curl "http://localhost:8080/api/v1/users/search?q=john&page=0&size=10"
```

### 7. Filter Users by Role

```bash
curl "http://localhost:8080/api/v1/users/filter/role/ADMIN?page=0&size=10"
```

Available roles: `ADMIN`, `MANAGER`, `USER`, `GUEST`, `DEVELOPER`, `ANALYST`

### 8. Filter Users by Status

```bash
curl "http://localhost:8080/api/v1/users/filter/status/ACTIVE?page=0&size=10"
```

Available statuses: `ACTIVE`, `INACTIVE`, `SUSPENDED`, `PENDING`

### 9. Filter Users by City

```bash
curl http://localhost:8080/api/v1/users/filter/city/NewYork
```

### 10. Filter Users by Country

```bash
curl http://localhost:8080/api/v1/users/filter/country/USA
```

### 11. Get User Statistics

```bash
curl http://localhost:8080/api/v1/users/stats
```

Response:
```json
{
  "activeUsers": 50,
  "inactiveUsers": 10,
  "suspendedUsers": 5,
  "pendingUsers": 3,
  "admins": 2,
  "managers": 5,
  "regularUsers": 61
}
```

## Database Schema

### Users Table

| Column        | Type         | Constraints                |
|---------------|--------------|----------------------------|
| id            | BIGSERIAL    | PRIMARY KEY                |
| first_name    | VARCHAR(100) | NOT NULL                   |
| last_name     | VARCHAR(100) | NOT NULL                   |
| email         | VARCHAR(150) | NOT NULL, UNIQUE           |
| phone         | VARCHAR(20)  |                            |
| address       | VARCHAR(200) |                            |
| city          | VARCHAR(100) |                            |
| country       | VARCHAR(100) |                            |
| postal_code   | VARCHAR(20)  |                            |
| role          | VARCHAR(20)  | NOT NULL                   |
| status        | VARCHAR(20)  | NOT NULL, DEFAULT 'ACTIVE' |
| bio           | VARCHAR(500) |                            |
| avatar_url    | VARCHAR(200) |                            |
| created_at    | TIMESTAMP    | NOT NULL                   |
| updated_at    | TIMESTAMP    | NOT NULL                   |
| last_login_at | TIMESTAMP    |                            |
| version       | BIGINT       | DEFAULT 0 (Optimistic Lock)|

### Indexes

- `idx_email` on email
- `idx_role` on role
- `idx_status` on status
- `idx_created_at` on created_at
- `idx_city` on city
- `idx_country` on country

## Kafka Events

The service publishes user events to Kafka topic `user-events`:

### Event Types
- `USER_CREATED` - When a new user is created
- `USER_UPDATED` - When user information is updated
- `USER_DELETED` - When a user is deleted
- `USER_STATUS_CHANGED` - When user status changes

### Event Schema
```json
{
  "eventType": "USER_CREATED",
  "userId": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "status": "ACTIVE",
  "eventTimestamp": "2025-11-17 10:30:45",
  "performedBy": "system"
}
```

## Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests UserServiceTest
```

### Generate Test Report
```bash
./gradlew test jacocoTestReport
```

The project includes:
- **Unit Tests** for services and controllers
- **Integration Tests** with Testcontainers
- **Repository Tests** with in-memory H2 database

## Deployment

### Deployed URL
The application is deployed and accessible at:

**Live API**: [https://user-management-service-production-9665.up.railway.app](https://user-management-service-production-9665.up.railway.app)

**Health Check**: [https://user-management-service-production-9665.up.railway.app/api/v1/health](https://user-management-service-production-9665.up.railway.app/api/v1/health)

**API Documentation**: [https://user-management-service-production-9665.up.railway.app/swagger-ui.html](https://user-management-service-production-9665.up.railway.app/swagger-ui.html)

### Deployment Options

#### 1. Render.com
```bash
# render.yaml is included in the project
# Simply connect your GitHub repository to Render
```

#### 2. Railway.app
```bash
# railway.json is included in the project
# Deploy with one click using Railway CLI
railway up
```

#### 3. Docker Hub
```bash
docker build -t GulnarMammadzada/user-management:latest .
docker push GulnarMammadzada/user-management:latest
```

#### 4. Kubernetes
```bash
# Kubernetes manifests can be generated from docker-compose.yml
kompose convert -f docker-compose.yml
kubectl apply -f .
```

## Configuration

### Environment Variables

| Variable                 | Description                | Default                                   |
|--------------------------|----------------------------|-------------------------------------------|
| PORT                     | Server port                | 8080                                      |
| DATABASE_URL             | PostgreSQL connection URL  | jdbc:postgresql://localhost:5432/usermanagement |
| DATABASE_USERNAME        | Database username          | postgres                                  |
| DATABASE_PASSWORD        | Database password          | postgres                                  |
| KAFKA_BOOTSTRAP_SERVERS  | Kafka broker address       | localhost:9092                            |
| BASE_URL                 | Application base URL       | http://localhost:8080                     |

### Application Profiles

- `default` - Local development
- `test` - Testing environment
- `prod` - Production environment

## API Response Codes

| Status Code | Description                |
|-------------|----------------------------|
| 200         | Success                    |
| 201         | Created                    |
| 204         | No Content (Delete)        |
| 400         | Bad Request (Validation)   |
| 404         | Not Found                  |
| 409         | Conflict (Duplicate Email) |
| 500         | Internal Server Error      |

## Error Response Format

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/users",
  "timestamp": "2025-11-17 10:30:45",
  "validationErrors": [
    {
      "field": "email",
      "message": "Email must be valid"
    }
  ]
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Contact

Project Link: [https://github.com/GulnarMammadzada/UserManagement](https://github.com/GulnarMammadzada/UserManagement)

---

**Made with Spring Boot 3 and modern Java technologies**
