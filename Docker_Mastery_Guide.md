# Docker Learning Guide

> **Personal DevOps Journey Documentation**  
> Version: 1.1.0 | Last Updated: 2025-12-30  
> This guide documents Docker commands, concepts, and real-world usage as I learn and apply Docker in my DevOps journey.

---

## Table of Contents

1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [Dockerfile Commands](#dockerfile-commands)
4. [Image Management](#image-management)
5. [Container Management](#container-management)
6. [Docker Hub & Registry](#docker-hub--registry)
7. [Docker Compose](#docker-compose)
8. [Real-World Examples](#real-world-examples)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)
11. [Future Learning Areas](#future-learning-areas)

---

## Introduction

### What is Docker?

Docker is a platform for developing, shipping, and running applications using containerization. Containers package an application with all its dependencies, ensuring it runs consistently across different environments.

### Why Docker?

- **Consistency**: "Works on my machine" → "Works everywhere"
- **Isolation**: Applications run in isolated environments
- **Portability**: Build once, run anywhere
- **Scalability**: Easy to scale applications horizontally
- **Resource Efficiency**: More efficient than virtual machines

### Key Terminology

- **Image**: A read-only template for creating containers (like a class in OOP)
- **Container**: A running instance of an image (like an object instance)
- **Dockerfile**: A text file with instructions to build an image
- **Registry**: A repository for Docker images (e.g., Docker Hub)
- **Docker Compose**: Tool for defining and running multi-container applications

---

## Core Concepts

### Image vs Container

```
Image (Template)          Container (Running Instance)
┌─────────────┐          ┌─────────────┐
│  Dockerfile │   →      │  Container  │
│  + Code     │   build  │  (running)  │
│  + Deps     │          │             │
└─────────────┘          └─────────────┘
```

**Analogy**: Image = Recipe, Container = Cooked Meal

### Multi-Stage Builds

Multi-stage builds allow you to use multiple `FROM` statements in a Dockerfile. Each stage can use a different base image and copy artifacts from previous stages. This results in smaller final images.

**Why use it?**
- Build tools (Maven, npm) are large and not needed at runtime
- Final image only contains runtime dependencies
- Significantly reduces image size

---

## Dockerfile Commands

### Basic Dockerfile Structure

```dockerfile
# Stage 1: Build frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# Stage 2: Build backend
FROM maven:3.9-eclipse-temurin-17 AS backend-builder
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src/ ./src/
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/
RUN mvn clean package -DskipTests -B

# Stage 3: Runtime
FROM tomcat:10.1-jdk17-temurin-jammy
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=backend-builder /app/target/api.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir -p /app/data && chmod 755 /app/data
WORKDIR /app
ENV DB_URL=jdbc:sqlite:/app/data/samples.db
ENV SERVER_PORT=8080
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/healthcheck || exit 1
CMD ["catalina.sh", "run"]
```

### Dockerfile Instructions Explained

| Instruction | Purpose | Example | When to Use |
|------------|---------|---------|-------------|
| `FROM` | Base image | `FROM node:20-alpine` | Start of every stage |
| `WORKDIR` | Set working directory | `WORKDIR /app` | Before COPY/RUN commands |
| `COPY` | Copy files into image | `COPY src/ ./src/` | Copy source code, configs |
| `RUN` | Execute command | `RUN npm install` | Install dependencies, build |
| `ENV` | Set environment variable | `ENV DB_URL=...` | Configuration values |
| `ARG` | Build-time variable | `ARG BASE_PATH=/` | Pass values during build |
| `EXPOSE` | Document port | `EXPOSE 8080` | Informational (not required) |
| `HEALTHCHECK` | Health check command | See example above | Monitor container health |
| `CMD` | Default command | `CMD ["catalina.sh", "run"]` | What runs when container starts |

### Build Arguments

```dockerfile
# In Dockerfile
ARG BASE_PATH=/
ENV VITE_BASE_PATH=${BASE_PATH}

# When building
docker build --build-arg BASE_PATH=/geological-sample-api/ -t myapp .
```

**Use Case**: Configure build-time settings (e.g., API endpoints, feature flags)

---

## Image Management

### Building Images

```bash
# Basic build
docker build -t image-name:tag .

# Build with build arguments
docker build --build-arg BASE_PATH=/api/ -t myapp:latest .

# Build with no cache (force rebuild)
docker build --no-cache -t myapp:latest .

# Build from specific Dockerfile
docker build -f Dockerfile.prod -t myapp:prod .
```

**Context**: Used when creating Dockerfile for Geological Sample API project
- **When**: After writing Dockerfile with multi-stage build
- **Why**: Convert Dockerfile into runnable image
- **Note**: The `.` at the end is the build context (current directory)

### Listing Images

```bash
# List all images
docker images

# List specific image
docker images geological-sample-api

# List with format (machine-readable)
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# Show only image IDs
docker images -q
```

**Context**: Used to verify image was built successfully
- **When**: After building image
- **Why**: Confirm image exists and check size

### Inspecting Images

```bash
# Show image details
docker inspect image-name:tag

# Show image history (layers)
docker history image-name:tag

# Show image size breakdown
docker images --format "{{.Repository}}:{{.Tag}} - {{.Size}}"
```

### Removing Images

```bash
# Remove specific image
docker rmi image-name:tag

# Remove by ID
docker rmi 0702eae72b20

# Remove all unused images
docker image prune

# Remove all images (careful!)
docker rmi $(docker images -q)
```

---

## Container Management

### Running Containers

```bash
# Run in foreground
docker run image-name:tag

# Run in detached mode (background)
docker run -d image-name:tag

# Run with port mapping
docker run -d -p 8080:8080 image-name:tag

# Run with environment variables
docker run -d -e DB_URL=jdbc:sqlite:/app/data/samples.db image-name:tag

# Run with volume mount
docker run -d -v ./data:/app/data image-name:tag

# Run with name
docker run -d --name my-container image-name:tag

# Run with all options
docker run -d \
  --name geological-api \
  -p 8080:8080 \
  -e DB_URL=jdbc:sqlite:/app/data/samples.db \
  -v ./data:/app/data \
  image-name:tag
```

**Context**: Used to start the Geological Sample API container
- **When**: After building image
- **Why**: Run the application in isolated environment
- **Port Mapping**: `-p host:container` maps host port to container port

### Listing Containers

```bash
# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# List with format
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Show only container IDs
docker ps -q
```

### Container Lifecycle

```bash
# Start stopped container
docker start container-name

# Stop running container
docker stop container-name

# Restart container
docker restart container-name

# Remove stopped container
docker rm container-name

# Remove running container (force)
docker rm -f container-name

# Remove all stopped containers
docker container prune
```

### Viewing Logs

```bash
# View logs
docker logs container-name

# Follow logs (like tail -f)
docker logs -f container-name

# Show last N lines
docker logs --tail 100 container-name

# Show logs with timestamps
docker logs -t container-name
```

### Executing Commands in Container

```bash
# Execute command in running container
docker exec container-name command

# Interactive shell
docker exec -it container-name /bin/bash

# Execute as specific user
docker exec -u root container-name command
```

**Use Case**: Debugging, inspecting files, running maintenance tasks

---

## Docker Hub & Registry

Docker registries store and distribute Docker images. Common registries include Docker Hub (public), Nexus (private), Harbor, and GitLab Container Registry.

### Tagging Images

```bash
# Tag for Docker Hub
docker tag local-image:tag username/repository:tag

# Tag with version
docker tag image:latest username/repo:1.0.0

# Tag multiple versions
docker tag image:latest username/repo:latest
docker tag image:latest username/repo:1.0.0
docker tag image:latest username/repo:v1.0.0
```

**Context**: Used to prepare image for Docker Hub push
- **When**: After building image locally
- **Why**: Docker Hub requires `username/repository:tag` format
- **Example**: `docker tag geological-sample-api:latest salamituns/geological-sample-api:latest`

### Authentication

```bash
# Login to Docker Hub
docker login

# Login to specific registry
docker login registry.example.com

# Login with username (non-interactive)
docker login -u username

# Logout
docker logout
```

**Context**: Required before pushing to Docker Hub
- **When**: Before first push to registry
- **Why**: Authentication required for push operations
- **Note**: Interactive login requires TTY (run in terminal, not script)

### Pushing Images

```bash
# Push to Docker Hub
docker push username/repository:tag

# Push all tags
docker push username/repository --all-tags

# Push to custom registry
docker push registry.example.com/repo:tag
```

**Context**: Used to publish image to Docker Hub
- **When**: After tagging and logging in
- **Why**: Make image available for others or deployment
- **Prerequisites**: 
  1. Repository must exist on Docker Hub (create via web UI)
  2. Must be logged in (`docker login`)
  3. Must have push permissions

### Pulling Images

```bash
# Pull from Docker Hub
docker pull username/repository:tag

# Pull latest (default)
docker pull username/repository

# Pull from custom registry
docker pull registry.example.com/repo:tag
```

### Nexus Docker Registry

Nexus Repository Manager can act as a Docker registry, but requires separate configuration from Maven repositories.

#### Setting Up Nexus Docker Registry

**Key Difference**: Maven repositories (port 8081) ≠ Docker registries (port 8083)

**Steps**:
1. **Create Docker Hosted Repository in Nexus**:
   - Nexus UI → Settings → Repositories → Create repository
   - Select: **docker (hosted)**
   - Configure: Name, HTTP port (e.g., 8083), Enable Docker V1 API
   - Create repository

2. **Configure Docker for Insecure Registry** (if using HTTP):
   ```json
   // Docker Desktop → Settings → Docker Engine
   {
     "insecure-registries": ["10.2.0.11:8083"]
   }
   ```
----------
After creating the repository, update your Docker insecure registry configuration to include port 8083:
- sudo mkdir -p /etc/docker
- echo '{"insecure-registries": ["10.2.0.11:8082", "10.2.0.11:8083"]}' | sudo tee /etc/docker/daemon.json
- sudo systemctl restart docker
-----------

3. **Login to Nexus**:
   ```bash
   docker login 10.2.0.11:8083
   ```

4. **Tag Image for Nexus**:
   ```bash
   # Format: <nexus-host>:<port>/<repository-name>/<image-name>:<tag>
   docker tag geological-sample-api:latest 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
   ```

5. **Push to Nexus**:
   ```bash
   docker push 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
   ```

**Context**: Used when pushing to private Nexus Docker registry
- **When**: After building image and setting up Nexus Docker repository
- **Why**: Private registry for internal deployments
- **Common Error**: 404 Not Found → Repository doesn't exist in Nexus
- **Solution**: Create Docker (hosted) repository in Nexus UI first

#### Complete Nexus Workflow

```bash
# 1. Build image
docker build -t geological-sample-api:latest .

# 2. Tag for Nexus (repository name must match Nexus config)
docker tag geological-sample-api:latest 10.2.0.11:8083/docker-hosted/geological-sample-api:v1

# 3. Login to Nexus
docker login 10.2.0.11:8083

# 4. Push to Nexus
docker push 10.2.0.11:8083/docker-hosted/geological-sample-api:v1

# 5. Pull from Nexus (later)
docker pull 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
```

**Important Notes**:
- Repository name in tag must match Nexus repository name exactly
- Docker registry is separate from Maven repositories
- Must configure insecure registry for HTTP (not HTTPS)
- Nexus user needs Docker repository read/write permissions

---

## Docker Compose

### Basic docker-compose.yml

```yaml
version: '3.8'

services:
  geological-sample-api:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        BASE_PATH: /
    container_name: geological-sample-api
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:sqlite:/app/data/samples.db
      - SERVER_PORT=8080
      - LOG_LEVEL=INFO
    volumes:
      - ./data:/app/data
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/healthcheck"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s
```

### Docker Compose Commands

```bash
# Build and start services
docker-compose up

# Start in detached mode
docker-compose up -d

# Build images
docker-compose build

# Start services (without building)
docker-compose start

# Stop services
docker-compose stop

# Stop and remove containers
docker-compose down

# View logs
docker-compose logs

# Follow logs
docker-compose logs -f

# Execute command in service
docker-compose exec service-name command

# Scale service
docker-compose up -d --scale service-name=3
```

**Context**: Used for managing multi-container applications
- **When**: Application has multiple services or dependencies
- **Why**: Simplifies orchestration of related containers
- **Benefits**: Single file defines entire stack

---

## Real-World Examples

### Example 1: Containerizing Geological Sample API

**Problem**: Need to containerize a Spring Boot + React application

**Solution**: Multi-stage Dockerfile

```dockerfile
# Stage 1: Build frontend (React)
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
ARG BASE_PATH=/
ENV VITE_BASE_PATH=${BASE_PATH}
RUN npm run build

# Stage 2: Build backend (Spring Boot)
FROM maven:3.9-eclipse-temurin-17 AS backend-builder
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src/ ./src/
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/
RUN mvn clean package -DskipTests -B

# Stage 3: Runtime (Tomcat)
FROM tomcat:10.1-jdk17-temurin-jammy
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=backend-builder /app/target/api.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir -p /app/data && chmod 755 /app/data
WORKDIR /app
ENV DB_URL=jdbc:sqlite:/app/data/samples.db
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/healthcheck || exit 1
CMD ["catalina.sh", "run"]
```

**Commands Used**:
```bash
# Build image
docker build -t geological-sample-api:latest .

# Verify build
docker images geological-sample-api:latest

# Tag for Docker Hub
docker tag geological-sample-api:latest salamituns/geological-sample-api:latest

# Login (run in terminal)
docker login

# Push to Docker Hub
docker push salamituns/geological-sample-api:latest
```

**Key Learnings**:
- Multi-stage builds reduce final image size significantly
- Build arguments allow configuration flexibility
- Health checks enable container orchestration tools to monitor status
- Volume mounts persist data outside container lifecycle

### Example 2: Pushing to Nexus Docker Registry

**Problem**: Need to push Docker image to private Nexus registry

**Initial Error**: 404 Not Found when pushing to `10.2.0.11:8083/geological-sample-api:v1`

**Root Cause**: Docker registry repository didn't exist in Nexus (only Maven repositories existed)

**Solution**: Create Docker (hosted) repository in Nexus

**Complete Workflow**:
```bash
# 1. Build image
docker build -t geological-sample-api:latest .

# 2. Configure Docker for insecure registry (HTTP)
# Docker Desktop → Settings → Docker Engine
# Add: { "insecure-registries": ["10.2.0.11:8083"] }
# Restart Docker Desktop

# 3. Tag for Nexus (include repository name)
docker tag geological-sample-api:latest 10.2.0.11:8083/docker-hosted/geological-sample-api:v1

# 4. Login to Nexus
docker login 10.2.0.11:8083
# Enter Nexus username and password

# 5. Push to Nexus
docker push 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
```

**Key Learnings**:
- Nexus Maven repositories (8081) ≠ Docker registries (8083)
- Must create Docker (hosted) repository separately in Nexus
- Tag format must include repository name: `host:port/repo-name/image:tag`
- Insecure registry configuration required for HTTP (not HTTPS)
- Repository name in tag must match Nexus repository name exactly

**Common Mistakes**:
- ❌ Assuming Maven repository works for Docker
- ❌ Forgetting to include repository name in tag
- ❌ Not configuring insecure registry for HTTP
- ❌ Using wrong port (8081 instead of 8083)

---

## Best Practices

### 1. Image Size Optimization

✅ **Do**:
- Use multi-stage builds
- Use `.dockerignore` to exclude unnecessary files
- Use Alpine Linux base images when possible
- Combine RUN commands to reduce layers
- Remove package manager cache in same layer

❌ **Don't**:
- Include build tools in final image
- Copy entire project directory
- Install unnecessary packages
- Leave temporary files

**Example**:
```dockerfile
# Good: Combine commands, clean up
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Bad: Separate commands
RUN apt-get update
RUN apt-get install -y curl
# Cache files remain
```

### 2. Security

✅ **Do**:
- Use specific image tags (not `latest` in production)
- Run containers as non-root user when possible
- Scan images for vulnerabilities
- Keep base images updated
- Use secrets management (not hardcode)

❌ **Don't**:
- Expose unnecessary ports
- Store secrets in images
- Use root user unless necessary
- Ignore security updates

### 3. Dockerfile Organization

✅ **Do**:
- Order instructions from least to most frequently changing
- Use COPY before RUN when possible (better caching)
- Document with comments
- Use ARG for build-time configuration
- Set WORKDIR early

**Example**:
```dockerfile
# Dependencies change less frequently
COPY package*.json ./
RUN npm ci

# Source code changes frequently
COPY src/ ./src/
```

### 4. Naming Conventions

✅ **Do**:
- Use descriptive names: `geological-sample-api:1.0.0`
- Follow semantic versioning
- Use tags: `latest`, `1.0.0`, `v1.0.0`
- Include project/team prefix

❌ **Don't**:
- Use generic names: `app`, `test`
- Rely only on `latest` tag
- Use random names

---

## Troubleshooting

### Common Issues & Solutions

#### 1. Build Fails: "Cannot find package"

**Problem**: Dependencies not installed

**Solution**:
```bash
# Ensure package files are copied before install
COPY package*.json ./
RUN npm install
COPY . .
```

#### 2. Container Exits Immediately

**Problem**: Main process terminates

**Solution**:
```bash
# Check logs
docker logs container-name

# Run interactively to debug
docker run -it image-name /bin/bash

# Ensure CMD/ENTRYPOINT is correct
```

#### 3. Port Already in Use

**Problem**: Host port already occupied

**Solution**:
```bash
# Check what's using the port
sudo lsof -i :8080

# Use different host port
docker run -p 8081:8080 image-name
```

#### 4. Permission Denied

**Problem**: File permission issues

**Solution**:
```bash
# Check file permissions in container
docker exec container-name ls -la /path

# Fix permissions in Dockerfile
RUN chmod 755 /app/data
RUN chown user:user /app/data
```

#### 5. Out of Disk Space

**Problem**: Too many images/containers

**Solution**:
```bash
# Clean up unused resources
docker system prune

# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune
```

#### 6. Push Fails: "authorization failed"

**Problem**: Not logged in or repository doesn't exist

**Solution**:
```bash
# Login first
docker login

# Create repository on Docker Hub via web UI
# Then push
docker push username/repo:tag
```

#### 7. Nexus Push Fails: "404 Not Found"

**Problem**: Docker registry repository doesn't exist in Nexus

**Error Message**:
```
404 - Sonatype Nexus Repository
Not Found
```

**Root Cause**: 
- Maven repositories (port 8081) are different from Docker registries (port 8083)
- Docker registry repository must be created separately in Nexus

**Solution**:
1. **Create Docker Hosted Repository**:
   - Nexus UI → Settings → Repositories → Create repository
   - Select: **docker (hosted)**
   - Name: `docker-hosted` (or your preferred name)
   - HTTP Port: `8083` (or your port)
   - Enable Docker V1 API: ✅
   - Create repository

2. **Configure Insecure Registry** (for HTTP):
   ```json
   // Docker Desktop → Settings → Docker Engine
   {
     "insecure-registries": ["10.2.0.11:8083"]
   }
   ```
   Restart Docker Desktop

3. **Verify Repository Name in Tag**:
   ```bash
   # Tag must include repository name
   docker tag image:latest 10.2.0.11:8083/docker-hosted/image:v1
   #                                    ^^^^^^^^^^^^
   #                                    Must match Nexus repo name
   ```

4. **Login and Push**:
   ```bash
   docker login 10.2.0.11:8083
   docker push 10.2.0.11:8083/docker-hosted/image:v1
   ```

**Context**: Encountered when pushing Geological Sample API to Nexus
- **When**: First attempt to push to Nexus Docker registry
- **Why**: Assumed Maven and Docker repositories were the same
- **Learning**: Nexus requires separate repository configuration for each format

#### 8. Nexus Push: "unauthorized: authentication required"

**Problem**: Authentication or permission issues

**Solution**:
```bash
# 1. Login to Nexus
docker login 10.2.0.11:8083

# 2. Verify Nexus user permissions:
#    - nx-repository-view-docker-docker-hosted-read
#    - nx-repository-view-docker-docker-hosted-write

# 3. Check Nexus Realms:
#    Nexus → Administration → Security → Realms
#    Ensure "Docker Bearer Token Realm" is active
```

#### 9. Nexus Push: "connection refused"

**Problem**: Cannot connect to Nexus registry

**Solution**:
```bash
# 1. Verify Nexus is running
curl http://10.2.0.11:8083/v2/

# 2. Check firewall rules
# 3. Verify port in Nexus repository settings
# 4. Ensure repository is started in Nexus UI
```

---

## Future Learning Areas

### Topics to Explore

- [ ] **Docker Networking**: Bridge, host, overlay networks
- [ ] **Docker Volumes**: Named volumes, bind mounts, tmpfs
- [ ] **Docker Swarm**: Container orchestration
- [ ] **Kubernetes**: Production orchestration
- [ ] **Docker Security**: Image scanning, secrets management
- [ ] **CI/CD Integration**: Docker in Jenkins, GitHub Actions
- [ ] **Multi-Architecture Builds**: ARM, AMD64 support
- [x] **Docker Registry**: Private registry setup (Nexus Docker registry)
- [ ] **Docker Compose Advanced**: Overrides, profiles, secrets
- [ ] **Container Optimization**: Resource limits, health checks

### Resources

- [Docker Official Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)
- [Best Practices Guide](https://docs.docker.com/develop/dev-best-practices/)
- [Dockerfile Reference](https://docs.docker.com/reference/dockerfile/)

---

## Version History

| Version | Date | Changes | Context |
|---------|------|---------|---------|
| 1.0.0 | 2025-12-30 | Initial version | Created during Geological Sample API containerization |
| 1.1.0 | 2025-12-30 | Added Nexus Docker registry section | Resolved 404 error when pushing to Nexus, learned about separate Docker registry setup |

---

## Notes & Observations

### Personal Insights

- **Multi-stage builds are powerful**: Reduced final image from ~800MB to ~267MB
- **Build context matters**: `.dockerignore` significantly speeds up builds
- **Health checks are essential**: Enable proper container orchestration
- **Volume mounts for persistence**: Database survives container restarts
- **Tagging strategy**: Use semantic versioning alongside `latest`
- **Nexus registries are separate**: Maven repositories ≠ Docker registries (different ports, different configs)
- **Repository names matter**: Tag format must include repository name for Nexus
- **Insecure registry config**: HTTP registries require explicit Docker configuration
- **404 errors are informative**: Usually means repository doesn't exist, not a network issue

### Questions to Explore Later

1. How do Docker networks work for multi-container apps?
2. What's the difference between Docker Swarm and Kubernetes?
3. How to implement blue-green deployments with Docker?
4. Best practices for Docker in production environments?
5. How to optimize Docker builds in CI/CD pipelines?
6. How to set up HTTPS for Nexus Docker registry?
7. How to implement image scanning in Nexus?
8. Best practices for Docker registry cleanup policies?

---

## Quick Reference

### Most Used Commands

```bash
# Build
docker build -t name:tag .

# Run
docker run -d -p 8080:8080 --name container name:tag

# Logs
docker logs -f container-name

# Stop/Start
docker stop container-name
docker start container-name

# Remove
docker rm container-name
docker rmi image-name:tag

# Compose
docker-compose up -d
docker-compose down
docker-compose logs -f

# Registry (Nexus)
docker login 10.2.0.11:8083
docker tag image:latest 10.2.0.11:8083/repo-name/image:tag
docker push 10.2.0.11:8083/repo-name/image:tag
docker pull 10.2.0.11:8083/repo-name/image:tag
```

---

*This document is a living guide. Update it as you learn and encounter new Docker concepts and challenges.*
