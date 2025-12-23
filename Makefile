.PHONY: build run test clean migrate docker-build help

# Default target
help:
	@echo "Available targets:"
	@echo "  make build        - Compile the project (mvn clean package)"
	@echo "  make run          - Run the application (mvn spring-boot:run)"
	@echo "  make test         - Run tests (mvn test)"
	@echo "  make clean        - Clean build artifacts (mvn clean)"
	@echo "  make migrate      - Run database migrations (handled by Flyway on startup)"
	@echo "  make docker-build - Build Docker image (for future milestone)"

# Build the project
build:
	@echo "Building the project..."
	@echo "Building frontend..."
	@cd frontend && npm install && npm run build
	@echo "Copying frontend build to static resources..."
	@mkdir -p src/main/resources/static
	@cp -r frontend/dist/* src/main/resources/static/
	@echo "Building backend..."
	mvn clean package

# Run the application
run:
	@echo "Running the application..."
	@mkdir -p data
	mvn spring-boot:run

# Run tests
test:
	@echo "Running tests..."
	mvn test

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	rm -rf data/
	rm -f *.db
	rm -rf src/main/resources/static/*
	rm -rf frontend/dist
	rm -rf frontend/node_modules

# Database migrations are handled automatically by Flyway on application startup
migrate:
	@echo "Database migrations are handled automatically by Flyway on application startup."
	@echo "To run migrations manually, start the application with 'make run'"

# Build Docker image (placeholder for future milestone)
docker-build:
	@echo "Docker build will be implemented in a future milestone"
	@echo "This will build a Docker image of the application"

