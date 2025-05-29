# Mini Doodle

A high-performance meeting scheduling platform built with Spring Boot that enables users to manage their time slots, schedule meetings, and view their calendar availability.

## Features

- **Time Slot Management**
  - Create available time slots with configurable duration
  - Delete or modify existing time slots
  - Mark time slots as busy or free
  - Personal calendar management for each user

- **Meeting Scheduling**
  - Convert available slots into meetings
  - Add meeting details (title, description, participants)
  - Query free/busy slots
  - View aggregated availability for selected time frames

## Tech Stack

- Java 21
- Spring Boot 3.x
- H2 Database (in-memory)
- Flyway for database migrations
- Maven for dependency management
- JUnit 5 for testing

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/minidoodle/
│   │       ├── config/         # Configuration classes
│   │       ├── model/         # Domain entities
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic
│   │       ├── controller/     # REST endpoints
│   │       └── MiniDoodleApplication.java
│   └── resources/
│       ├── application.yml     # Application configuration
│       └── db/migration/       # Flyway migration scripts
└── test/                       # Test classes
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8.x or higher

### Setup

1. Clone the repository
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Access the H2 Console at http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:minidoodle`
   - Username: `sa`
   - Password: (leave empty)

## API Documentation

API documentation will be available at `/swagger-ui.html` when running the application.