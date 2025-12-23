# Geological Sample Management REST API

A production-ready REST API for managing geological samples collected during field surveys. This API provides CRUD operations for cataloging rock samples, minerals, soil samples, fossils, sediments, and other geological specimens with associated metadata including location, collection date, sample type, and storage information.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Local Setup](#local-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Best Practices](#best-practices)

## Features

- **CRUD Operations**: Create, read, update, and delete geological samples
- **API Versioning**: RESTful API with versioning support (`/api/v1/`)
- **Database Migrations**: Automated schema management using Flyway
- **Input Validation**: Comprehensive validation for all request payloads
- **Error Handling**: Consistent error responses with appropriate HTTP status codes
- **Health Check**: Health check endpoint for monitoring
- **Structured Logging**: Meaningful logs with appropriate log levels
- **Environment Configuration**: Configuration via environment variables (Twelve-Factor App compliant)
- **Unit Tests**: Comprehensive test coverage for all endpoints
- **Frontend Demo**: React + TypeScript frontend application for easy API interaction

## Prerequisites

- **Java 17+** - Required for running the application
- **Maven 3.6+** - Required for building and dependency management
- **Make** (optional) - For using Makefile commands

## Local Setup

### 1. Clone the Repository

```bash
cd /path/to/your/workspace
git clone <repository-url>
cd geological-sample-api
```

### 2. Configure Environment Variables

Create a `.env` file in the project root (or export environment variables):

```bash
# Database Configuration
export DB_URL=jdbc:sqlite:./data/samples.db

# Server Configuration
export SERVER_PORT=8080

# Logging Configuration
export LOG_LEVEL=INFO
```

Alternatively, you can use the provided `.env.example` as a template:

```bash
cp .env.example .env
# Edit .env with your configuration
```

### 3. Build the Project

```bash
make build
# or
mvn clean package
```

### 4. Run Database Migrations

Database migrations are handled automatically by Flyway when the application starts. The migration script will create the `samples` table and necessary indexes.

### 5. Run the Application

```bash
make run
# or
mvn spring-boot:run
```

The API will be available at `http://localhost:8080` (or the port specified in `SERVER_PORT`).

## Configuration

### Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `DB_URL` | SQLite database file path | `jdbc:sqlite:./data/samples.db` |
| `SERVER_PORT` | Server port | `8080` |
| `LOG_LEVEL` | Logging level (DEBUG, INFO, WARN, ERROR) | `INFO` |
| `SHOW_SQL` | Show SQL queries in logs | `false` |

### Application Configuration

The application uses `application.yml` for configuration, which reads values from environment variables. All database, server, and logging configurations can be overridden via environment variables.

## Running the Application

### Using Makefile

```bash
# Build the project
make build

# Run the application
make run

# Run tests
make test

# Clean build artifacts
make clean
```

### Using Maven Directly

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Clean build artifacts
mvn clean
```

## API Documentation

### Base URL

```
http://localhost:8080
```

### Endpoints

#### Health Check

**GET** `/healthcheck`

Check if the API is running and responsive.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-27T10:30:00",
  "service": "Geological Sample Management API"
}
```

#### Create Sample

**POST** `/api/v1/samples`

Create a new geological sample.

**Request Body:**
```json
{
  "sampleIdentifier": "GS-2024-001",
  "sampleName": "Granite Sample",
  "sampleType": "ROCK",
  "collectionDate": "2024-01-15",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "locationName": "Central Park",
  "collectorName": "Dr. Jane Smith",
  "description": "Fine-grained granite sample",
  "storageLocation": "Lab-A-Shelf-12"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "sampleIdentifier": "GS-2024-001",
  "sampleName": "Granite Sample",
  "sampleType": "ROCK",
  "collectionDate": "2024-01-15",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "locationName": "Central Park",
  "collectorName": "Dr. Jane Smith",
  "description": "Fine-grained granite sample",
  "storageLocation": "Lab-A-Shelf-12",
  "createdAt": "2024-01-27T10:30:00",
  "updatedAt": "2024-01-27T10:30:00"
}
```

#### Get All Samples

**GET** `/api/v1/samples`

Retrieve all samples with pagination support.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field and direction (e.g., `id,desc`)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "sampleIdentifier": "GS-2024-001",
      "sampleName": "Granite Sample",
      ...
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

#### Get Sample by ID

**GET** `/api/v1/samples/{id}`

Retrieve a specific sample by its ID.

**Response:** `200 OK`
```json
{
  "id": 1,
  "sampleIdentifier": "GS-2024-001",
  "sampleName": "Granite Sample",
  ...
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2024-01-27T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Sample not found with id: 1"
}
```

#### Update Sample

**PUT** `/api/v1/samples/{id}`

Update an existing sample.

**Request Body:** (same as Create Sample)

**Response:** `200 OK` (same format as Create Sample)

#### Delete Sample

**DELETE** `/api/v1/samples/{id}`

Delete a sample by its ID.

**Response:** `204 No Content`

### Sample Types

The API supports the following sample types:
- `ROCK`
- `MINERAL`
- `SOIL`
- `FOSSIL`
- `SEDIMENT`
- `OTHER`

### Error Responses

All error responses follow a consistent format:

```json
{
  "timestamp": "2024-01-27T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "sampleIdentifier": "Sample identifier is required",
    "sampleName": "Sample name is required"
  }
}
```

## Frontend Demo

A modern React + TypeScript frontend application is included in the `frontend/` directory, providing a user-friendly interface for interacting with the API.

### Features

- **Complete CRUD Operations**: Create, read, update, and delete samples through an intuitive UI
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Pagination**: Efficiently browse through large collections of samples
- **Form Validation**: Client-side validation with helpful error messages
- **Modern UI**: Clean, accessible interface built with Tailwind CSS

### Prerequisites

- **Node.js 18+** - Required for running the frontend
- **npm** or **yarn** - Package manager

### Quick Start

1. **Start the Backend API** (in project root):
   ```bash
   make run
   ```

2. **Start the Frontend** (in a new terminal):
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Access the Application**:
   - Frontend: `http://localhost:5173`
   - Backend API: `http://localhost:8080`

### Frontend Technology Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client
- **Headless UI** - Accessible UI components

### CORS Configuration

The backend has been configured to allow requests from the frontend. The CORS configuration (see `CorsConfig.java`) allows:
- Origins: `http://localhost:5173` (Vite default) and `http://localhost:3000`
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: All headers
- Credentials: Enabled

For more details about the frontend, see the [Frontend README](frontend/README.md).

## Testing

### Run All Tests

```bash
make test
# or
mvn test
```

### Test Coverage

The project includes comprehensive unit tests for:
- **Controller Layer**: All REST endpoints tested with MockMvc
- **Service Layer**: Business logic tested with mocked repository
- **Error Handling**: Validation and exception scenarios

### Test Structure

```
src/test/java/com/geoscience/sampleapi/
├── controller/
│   ├── SampleControllerTest.java
│   └── HealthCheckControllerTest.java
└── service/
    └── SampleServiceTest.java
```

## Project Structure

```
geological-sample-api/
├── src/
│   ├── main/
│   │   ├── java/com/geoscience/sampleapi/
│   │   │   ├── GeologicalSampleApiApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── SampleController.java
│   │   │   │   └── HealthCheckController.java
│   │   │   ├── model/
│   │   │   │   └── GeologicalSample.java
│   │   │   ├── repository/
│   │   │   │   └── SampleRepository.java
│   │   │   ├── service/
│   │   │   │   └── SampleService.java
│   │   │   ├── dto/
│   │   │   │   ├── SampleRequest.java
│   │   │   │   └── SampleResponse.java
│   │   │   ├── config/
│   │   │   │   └── DatabaseConfig.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── SampleNotFoundException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           └── V1__Create_samples_table.sql
│   └── test/
│       └── java/com/geoscience/sampleapi/
│           ├── controller/
│           └── service/
├── frontend/                    # React frontend application
│   ├── src/
│   ├── package.json
│   └── README.md
├── pom.xml
├── Makefile
├── README.md
├── .gitignore
└── postman/
    └── Geological_Sample_API.postman_collection.json
```

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Database access layer
- **SQLite** - Database (easily migratable to PostgreSQL)
- **Flyway** - Database migration tool
- **Lombok** - Reduce boilerplate code
- **Maven** - Build and dependency management
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework

## Best Practices

This project follows several best practices:

1. **RESTful Design**: Proper HTTP verbs, status codes, and resource-based URLs
2. **API Versioning**: `/api/v1/` prefix for future compatibility
3. **DTO Pattern**: Separation of API contract from internal entity model
4. **Input Validation**: Comprehensive validation using Jakarta Validation
5. **Error Handling**: Consistent error responses with appropriate HTTP status codes
6. **Logging**: Structured logging with appropriate log levels
7. **Environment Configuration**: Configuration via environment variables (Twelve-Factor App)
8. **Database Migrations**: Version-controlled schema changes using Flyway
9. **Unit Testing**: Comprehensive test coverage for all layers
10. **Documentation**: Clear README and API documentation

## Twelve-Factor App Compliance

This application adheres to the [Twelve-Factor App](https://12factor.net/) methodology:

1. ✅ **Codebase** - Single codebase in Git
2. ✅ **Dependencies** - Explicitly declared in `pom.xml`
3. ✅ **Config** - Configuration via environment variables
4. ✅ **Backing Services** - Database as attached resource
5. ✅ **Build/Release/Run** - Separated via Maven and Makefile
6. ✅ **Processes** - Stateless application processes
7. ✅ **Port Binding** - Configurable via environment variable
8. ✅ **Concurrency** - Stateless design supports horizontal scaling
9. ✅ **Disposability** - Fast startup/shutdown
10. ✅ **Dev/Prod Parity** - Same codebase, different configs
11. ✅ **Logs** - Structured logging to stdout
12. ✅ **Admin Processes** - Migration handled by Flyway

## Next Steps

This is Part 1 of the journey. Future milestones include:

- **Part 2**: Docker containerization
- **Part 3**: CI/CD pipeline setup
- **Part 4**: Production deployment
- **Part 5**: Observability (metrics, tracing, monitoring)

## License

This project is part of a learning journey for building production-ready REST APIs.

## Support

For issues or questions, please refer to the project documentation or create an issue in the repository.

