# EatClub Restaurant Deals API

REST API for querying restaurant deals and calculating peak availability times.

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Building the Project

```bash
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Running the Application

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

## API Endpoints

### Get Active Deals
Returns all deals active at a specified time of day.

```
GET /api/v1/deals?timeOfDay={time}
```

**Parameters:**
- `timeOfDay` - Time in 12-hour (3:00pm) or 24-hour (15:00) format
- `page` - Page number (optional, default: 0)
- `size` - Page size (optional, default: 20)

**Example:**
```bash
curl "http://localhost:8080/api/v1/deals?timeOfDay=6:00pm"
```

### Get Peak Time Window
Calculates when the maximum number of deals are simultaneously available.

```
GET /api/v1/deals/peak-time
```

**Example:**
```bash
curl "http://localhost:8080/api/v1/deals/peak-time"
```

## API Documentation

Swagger UI is available at: **http://localhost:8080/swagger-ui.html**

OpenAPI spec: **http://localhost:8080/v3/api-docs**


## Technology Stack

- Spring Boot 3.5
- Spring WebFlux (for external API calls)
- Lombok
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito
