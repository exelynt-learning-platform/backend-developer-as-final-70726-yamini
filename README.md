# Resource Booking System REST API

A robust Spring Boot REST API for managing and booking resources. It features stateless JWT authentication, Role-Based Access Control (RBAC), dynamic filtering, pagination, comprehensive error handling, and MySQL persistence.

## Key Features

- **Secure Authentication**: Stateless JWT (JSON Web Token) authentication.
- **Role-Based Access Control**: Distinguishes between `ADMIN` (create/manage resources) and `USER` (book resources) roles.
- **Resource Management**: Endpoints to dynamically search, filter, and sort available resources using Spring Data Specifications.
- **Reservation System**: Users can book resources for specific time slots; conflict prevention is built-in.
- **Robust Error Handling**: Global exception handling via `@ControllerAdvice` returning standardized API error responses.
- **High Test Coverage**: Self-sufficient integration and unit tests covering all layers.

## Prerequisites
- **Java 17+**
- **Maven**
- **Docker** (optional, for running a local MySQL instance)

## Getting Started

### 1. Database Setup
Start a local MySQL instance (via Docker or local installation). A `docker-compose.yml` is provided for convenience:
```bash
docker-compose up -d
```

### 2. Build and Run
You can package the application into a JAR and run it, or run it directly using the Maven plugin:
```bash
# Build and package
mvn clean package

# Run the compiled JAR
java -jar target/*.jar
```
*Alternatively, run directly with Maven:*
```bash
mvn spring-boot:run
```

## Default Credentials & Test Data
The application will automatically seed the database on startup (if `app.seed.enabled=true`). You can use these accounts to interact with the API:
- **Admin User**: `admin@example.com` / `adminpass`
- **Standard User**: `user@example.com` / `userpass`

## API Documentation
Interactive OpenAPI (Swagger) documentation is available out-of-the-box. Once the application is running, navigate to:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Authentication Guide

**1. Login to obtain a JWT:**
```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"adminpass"}'
```
*The response will include a `"token"` field.*

**2. Use the token to access protected endpoints:**
```bash
curl -H "Authorization: Bearer <YOUR_TOKEN>" http://localhost:8080/api/resources
```

## Running Tests
The project contains a comprehensive suite of unit and integration tests (using JUnit 5 and Mockito). The integration tests are self-sufficient and seed their own temporary database contexts.

To execute the test suite:
```bash
mvn clean test
```

## Configuration & Security Notes
- **JWT Secret**: Configured in `application.properties`. For local development, a static fallback (`somerandomloremipsumdolorsitamet`) is used. **In production, ALWAYS override this by setting the `JWT_SECRET` environment variable.**
- **Database Credentials**: By default, it connects to a local `resource_booking_db` MySQL database. Set `DB_USERNAME` and `DB_PASSWORD` environment variables to override the defaults.
