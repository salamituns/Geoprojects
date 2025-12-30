# Geological Sample Management REST API

A production-ready REST API for managing geological samples collected during field surveys. This API provides CRUD operations for cataloging rock samples, minerals, soil samples, fossils, sediments, and other geological specimens with associated metadata including location, collection date, sample type, and storage information.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Local Setup](#local-setup)
- [Docker Deployment](#docker-deployment)
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
- **Docker** (optional) - For containerized deployment (Docker 20.10+ and Docker Compose 2.0+)

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

## Docker Deployment

The application can be containerized and deployed using Docker. The project includes a multi-stage Dockerfile that builds both the frontend and backend, and a `docker-compose.yml` file for easy deployment.

### Prerequisites

- **Docker 20.10+** - Container runtime
- **Docker Compose 2.0+** (optional) - For simplified container orchestration

### Quick Start with Docker Compose

The easiest way to run the application in Docker:

```bash
# Build and start the container
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the container
docker-compose down
```

The application will be available at:
- **Frontend**: `http://localhost:8080/`
- **Health Check**: `http://localhost:8080/healthcheck`
- **API**: `http://localhost:8080/api/v1/samples`

### Building Docker Image

#### Standard Build (Root Context Path)

```bash
# Build the Docker image
docker build -t geological-sample-api:latest .

# Run the container
docker run -d \
  --name geological-sample-api \
  -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  -e DB_URL=jdbc:sqlite:/app/data/samples.db \
  geological-sample-api:latest
```

#### Build with Custom Base Path

If deploying with a context path (e.g., `/geological-sample-api/`):

```bash
# Build with custom base path
docker build \
  --build-arg BASE_PATH=/geological-sample-api/ \
  -t geological-sample-api:latest .

# Run the container
docker run -d \
  --name geological-sample-api \
  -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  -e DB_URL=jdbc:sqlite:/app/data/samples.db \
  geological-sample-api:latest
```

### Docker Compose Configuration

The `docker-compose.yml` file provides a convenient way to manage the containerized application:

```yaml
services:
  geological-sample-api:
    build:
      context: .
      args:
        BASE_PATH: /  # Change to /geological-sample-api/ if needed
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:sqlite:/app/data/samples.db
      - SERVER_PORT=8080
      - LOG_LEVEL=INFO
    volumes:
      - ./data:/app/data  # Persists database data
```

#### Customizing Docker Compose

You can customize the deployment by modifying `docker-compose.yml`:

```bash
# Override base path during build
BASE_PATH=/geological-sample-api/ docker-compose up -d

# Use different port
docker-compose up -d --build
# Then modify ports in docker-compose.yml: "9090:8080"
```

### Environment Variables

The following environment variables can be configured when running the container:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `DB_URL` | SQLite database file path | `jdbc:sqlite:/app/data/samples.db` |
| `SERVER_PORT` | Server port (internal) | `8080` |
| `LOG_LEVEL` | Logging level (DEBUG, INFO, WARN, ERROR) | `INFO` |
| `CATALINA_OPTS` | Tomcat JVM options | `-Xmx512m -Xms256m` |

### Volume Mounts

The Docker setup uses volume mounts to persist data:

- **Database**: `./data:/app/data` - Persists SQLite database file
- **Logs**: Logs are written to stdout/stderr (use `docker logs` to view)

### Health Checks

The Docker image includes a health check that verifies the application is running:

```bash
# Check container health status
docker ps

# View health check logs
docker inspect geological-sample-api | grep -A 10 Health
```

The health check endpoint is available at `/healthcheck` and runs every 30 seconds.

### Docker Image Details

The Dockerfile uses a multi-stage build process:

1. **Frontend Builder**: Builds the React frontend with Node.js
2. **Backend Builder**: Builds the Spring Boot WAR with Maven
3. **Runtime**: Tomcat 10.1 with JDK 17 for running the application

This approach results in a smaller final image by excluding build tools from the runtime image.

### Troubleshooting Docker Deployment

#### Container Won't Start

```bash
# Check container logs
docker logs geological-sample-api

# Check if port is already in use
lsof -i :8080

# Verify Docker is running
docker ps
```

#### Database Permission Issues

```bash
# Ensure data directory has correct permissions
mkdir -p ./data
chmod 755 ./data

# Check container file permissions
docker exec geological-sample-api ls -la /app/data
```

#### Frontend Not Loading

If the frontend shows a blank screen:

1. **Check build base path**: Ensure `BASE_PATH` matches your deployment context
2. **Rebuild with correct path**: 
   ```bash
   docker build --build-arg BASE_PATH=/your-context-path/ -t geological-sample-api:latest .
   ```
3. **Check browser console**: Look for 404 errors on asset files

#### API Calls Fail

If API calls return 404:

1. **Verify context path**: Check if API is at `/api/v1/samples` or `/your-context-path/api/v1/samples`
2. **Check CORS configuration**: Ensure frontend base path matches API context
3. **View application logs**: `docker logs geological-sample-api`

### Production Considerations

For production deployments, consider:

1. **Use specific image tags**: Avoid `latest` tag in production
   ```bash
   docker build -t geological-sample-api:v1.0.0 .
   ```

2. **Resource limits**: Set memory and CPU limits
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1'
         memory: 1G
   ```

3. **Health checks**: The image includes health checks, but you can customize them
   ```yaml
   healthcheck:
     test: ["CMD", "curl", "-f", "http://localhost:8080/healthcheck"]
     interval: 30s
     timeout: 3s
     retries: 3
   ```

4. **Database backups**: Regularly backup the SQLite database file from `./data/samples.db`

5. **Log management**: Configure log rotation and aggregation
   ```bash
   docker run --log-driver json-file --log-opt max-size=10m --log-opt max-file=3 ...
   ```

### Docker Commands Reference

```bash
# Build image
docker build -t geological-sample-api:latest .

# Run container
docker run -d -p 8080:8080 -v $(pwd)/data:/app/data geological-sample-api:latest

# View logs
docker logs -f geological-sample-api

# Stop container
docker stop geological-sample-api

# Remove container
docker rm geological-sample-api

# Remove image
docker rmi geological-sample-api:latest

# Execute commands in container
docker exec -it geological-sample-api bash

# View container stats
docker stats geological-sample-api
```

For more details on Tomcat deployment, see [DEPLOYMENT.md](DEPLOYMENT.md).

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
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── README.md
├── DEPLOYMENT.md
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

This project includes Docker containerization. Future milestones include:

- **Part 3**: CI/CD pipeline setup
- **Part 4**: Production deployment
- **Part 5**: Observability (metrics, tracing, monitoring)

## License

This project is part of a learning journey for building production-ready REST APIs.

## Support

For issues or questions, please refer to the project documentation or create an issue in the repository.

