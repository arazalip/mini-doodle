# Mini Doodle

A high-performance meeting scheduling platform built with Spring Boot that enables users to manage their time slots, schedule meetings, and view their calendar availability.

## Features

- **User Management**
  - Create and manage user profiles
  - Update user information
  - Delete user accounts

- **Time Slot Management**
  - Create available time slots with configurable duration
  - Delete or modify existing time slots
  - Mark time slots as busy or free
  - Personal calendar management for each user

- **Meeting Scheduling**
  - Create and manage meetings
  - Add/remove meeting participants
  - Accept meeting invitations
  - Email notifications for meeting invitations and acceptances
  - View meeting details and participant status

## Tech Stack

- Java 21
- Spring Boot 3.3.3
- PostgreSQL 16
- Flyway for database migrations
- Maven for dependency management
- Docker & Docker Compose for containerization
- SpringDoc OpenAPI for API documentation
- JUnit 5 & TestContainers for testing

## Prerequisites

- Java 21 or higher
- Maven 3.8.x or higher
- Docker and Docker Compose
- PostgreSQL 16 (if running without Docker)

## Getting Started

### Environment Setup

1. Create a `.env` file in the project root with the following variables:
```env
POSTGRES_DB=minidoodle
POSTGRES_USER=minidoodle
POSTGRES_PASSWORD=minidoodle
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
```

### Running with Docker Compose (Recommended)

1. Build and start the containers:
```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`

### Running Locally

1. Start PostgreSQL database:
```bash
docker run --name mini-doodle-db \
  -e POSTGRES_DB=minidoodle \
  -e POSTGRES_USER=minidoodle \
  -e POSTGRES_PASSWORD=minidoodle \
  -p 5432:5432 \
  -d postgres:16-alpine

```

2. Build and run the application:
```bash
./mvnw clean package
./mvnw spring-boot:run
```

## API Documentation

The API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/api-docs`

### Key Endpoints

#### User Management
- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users` - Get all users
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

#### Meeting Management
- `POST /api/meetings` - Create a new meeting
- `GET /api/meetings/{id}` - Get meeting by ID
- `PUT /api/meetings/{id}` - Update meeting
- `DELETE /api/meetings/{id}` - Delete meeting
- `POST /api/meetings/{id}/participants/{userId}` - Add participant
- `DELETE /api/meetings/{id}/participants/{userId}` - Remove participant
- `POST /api/meetings/{id}/accept/{participantId}` - Accept meeting invitation

#### Time Slot Management
- `POST /api/timeslots` - Create a new time slot
- `GET /api/timeslots/{id}` - Get time slot by ID
- `PUT /api/timeslots/{id}` - Update time slot
- `DELETE /api/timeslots/{id}` - Delete time slot
- `GET /api/timeslots/user/{userId}` - Get user's time slots

## Testing

Run the test suite:
```bash
./mvnw test
```

The project uses:
- JUnit 5 for unit testing
- TestContainers for integration testing
- Mockito for mocking dependencies

## Monitoring

The application exposes several monitoring endpoints through Spring Boot Actuator:
- Health check: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Environment: `http://localhost:8080/actuator/env`
- Loggers: `http://localhost:8080/actuator/loggers`

## Areas for Improvement
The current implementation handles time slots as fixed blocks. Here's how we could improve it with dynamic slot management:
When a meeting is scheduled within a larger time slot, the system could automatically split the slot into smaller segments. When adjacent slots become available, the system could automatically merge them.
Benefits:
  - More efficient use of calendar space
  - Automatic creation of smaller slots for other meetings
  - Better visibility of available time segments
  - Reduced manual slot management

### Other improvement suggestions
  - Email mocking should be replaced for production environment
  - Implement OAuth2/OpenID Connect for secure authentication
  - Implement JWT token-based authentication
  - Encrypt sensitive data at rest (e.g., user emails, personal information)
  - Add caching layer for frequently accessed data
  - Add pagination for list endpoints
  - Add different types of notifications
  - Implement email templates for notifications