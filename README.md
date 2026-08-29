# Resource Booking System

Spring Boot REST API for booking resources with JWT authentication, ADMIN/USER roles, reservations, filtering, pagination, and MySQL persistence.

Prerequisites
- Java 17+
- Maven
- Docker (optional, for local DB)

Quick start (with Docker MySQL)

1. Start MySQL:

```bash
docker-compose up -d
```

2. Build and run the app:

```bash
mvn clean package
java -jar target/*.jar
```

Or run with Maven:

```bash
mvn spring-boot:run
```

Default DB and credentials
- DB: `jdbc:mysql://localhost:3306/resource_booking_db`
- Seeded users (created at startup):
  - ADMIN: admin@example.com / adminpass
  - USER: user@example.com / userpass

Configuration
Edit `src/main/resources/application.properties` to change DB connection, JWT secret, or JPA settings. The project currently contains a MySQL configuration.

Auth example

1) Login to obtain JWT:

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"adminpass"}'
```

Response includes `token`.

2) Use token to call protected endpoints:

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/resources
```

API docs
- OpenAPI available at `/v3/api-docs` and Swagger UI at `/swagger-ui.html` when running.

Notes
- JWT secret and expiration are in `application.properties` under `app.jwt.*` for convenience; rotate in production.
- For PostgreSQL, replace the JDBC URL and driver in `application.properties` and add the Postgres connector dependency.
