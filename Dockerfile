# Multi-stage Dockerfile for Geological Sample API
# Stage 1: Build frontend
FROM node:20-alpine AS frontend-builder

WORKDIR /app/frontend

# Copy frontend package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci

# Copy frontend source
COPY frontend/ ./

# Build frontend (default base path is /)
# Can be overridden with --build-arg BASE_PATH=/geological-sample-api/
ARG BASE_PATH=/
ENV VITE_BASE_PATH=${BASE_PATH}

RUN npm run build

# Stage 2: Build backend
FROM maven:3.9-eclipse-temurin-17 AS backend-builder

WORKDIR /app

# Copy Maven configuration
COPY pom.xml ./

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ ./src/

# Copy frontend build from previous stage
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 3: Runtime with Tomcat
FROM tomcat:10.1-jdk17-temurin-jammy

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file to Tomcat
COPY --from=backend-builder /app/target/api.war /usr/local/tomcat/webapps/ROOT.war

# Create directory for SQLite database
RUN mkdir -p /app/data && chmod 755 /app/data

# Set working directory
WORKDIR /app

# Environment variables
ENV DB_URL=jdbc:sqlite:/app/data/samples.db
ENV SERVER_PORT=8080
ENV LOG_LEVEL=INFO
ENV CATALINA_OPTS="-Xmx512m -Xms256m"

# Expose port
EXPOSE 8080

# Health check (deployed as ROOT.war, so context path is /)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/healthcheck || exit 1

# Start Tomcat
CMD ["catalina.sh", "run"]
