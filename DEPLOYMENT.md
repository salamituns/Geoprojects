# Deployment Guide

## Building for Tomcat Deployment

### With Context Path (e.g., `/geological-sample-api/`)

When deploying to Tomcat with a context path, you need to build the frontend with the correct base path:

```bash
# Build with context path
BASE_PATH=/geological-sample-api/ make build

# Or using Maven directly
cd frontend
VITE_BASE_PATH=/geological-sample-api/ npm run build
cd ..
mkdir -p src/main/resources/static
cp -r frontend/dist/* src/main/resources/static/
mvn clean package
```

### Without Context Path (Root Deployment)

If deploying as `ROOT.war` (accessible at root `/`):

```bash
# Build without context path (default)
make build

# Or explicitly
BASE_PATH=/ make build
```

## Context Path Configuration

The context path is determined by the WAR filename in Tomcat:

- `geological-sample-api-1.0.0-SNAPSHOT.war` → `/geological-sample-api-1.0.0-SNAPSHOT/`
- `api.war` → `/api/`
- `ROOT.war` → `/` (root)

## Environment Variables for Tomcat

When deploying to Tomcat, set these environment variables in `setenv.sh` (or `setenv.bat` on Windows):

```bash
# Database path (adjust to your Tomcat installation)
export DB_URL=jdbc:sqlite:/opt/tomcat/data/samples.db

# Server port (if different from default 8080)
export SERVER_PORT=8080

# Logging level
export LOG_LEVEL=INFO
```

Make sure the database directory exists:
```bash
mkdir -p /opt/tomcat/data
chmod 755 /opt/tomcat/data
```

## Verification

After deployment, verify:

1. **Frontend loads**: `http://your-server:port/geological-sample-api/`
2. **Health check**: `http://your-server:port/geological-sample-api/healthcheck`
3. **API endpoint**: `http://your-server:port/geological-sample-api/api/v1/samples`

## Troubleshooting

### Blank Screen / 404 on Assets

If you see a blank screen and assets return 404:
- **Problem**: Frontend was built without the correct base path
- **Solution**: Rebuild with `BASE_PATH=/geological-sample-api/ make build`

### API Calls Fail

If API calls return 404:
- **Problem**: API base URL not configured correctly
- **Solution**: The API service automatically uses the base path from Vite. Ensure you built with the correct `BASE_PATH`.

### Database Connection Errors

If you see database connection errors:
- **Problem**: Database path doesn't exist or is incorrect
- **Solution**: 
  1. Create the database directory: `mkdir -p /opt/tomcat/data`
  2. Set `DB_URL` environment variable in Tomcat's `setenv.sh`
  3. Ensure Tomcat has write permissions to the directory

