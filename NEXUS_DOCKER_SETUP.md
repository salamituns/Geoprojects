# Nexus Docker Registry Setup Guide

## Problem
Getting 404 error when pushing Docker images to Nexus at `10.2.0.11:8083/geological-sample-api:v1`

## Root Cause
Nexus requires a **separate Docker registry repository** to be created. Maven repositories (port 8081) are different from Docker registries (port 8083).

## Solution Steps

### Step 1: Create Docker Registry Repository in Nexus

1. **Login to Nexus UI**:
   - Navigate to: `http://10.2.0.11:8081` (or your Nexus URL)
   - Login with admin credentials

2. **Create Docker Hosted Repository**:
   - Go to: **Settings** (gear icon) → **Repositories** → **Create repository**
   - Select: **docker (hosted)**
   - Configure:
     - **Name**: `docker-hosted` (or `docker-registry`)
     - **HTTP**: Port `8083` (or your preferred port)
     - **Enable Docker V1 API**: ✅ (recommended for compatibility)
     - **Storage**: Choose a blob store (or create new one)
   - Click **Create repository**

3. **Verify Repository**:
   - The repository should now be accessible at: `http://10.2.0.11:8083`

### Step 2: Configure Docker to Use Nexus Registry

#### Option A: Configure Insecure Registry (HTTP)

Since you're using HTTP (not HTTPS), Docker needs to be configured to allow insecure registries.

**On macOS (Docker Desktop)**:

1. Open **Docker Desktop**
2. Go to **Settings** → **Docker Engine**
3. Add this configuration:
   ```json
   {
     "insecure-registries": ["10.2.0.11:8083"]
   }
   ```
4. Click **Apply & Restart**

**On Linux**:
```bash
# Edit /etc/docker/daemon.json
sudo nano /etc/docker/daemon.json

# Add:
{
  "insecure-registries": ["10.2.0.11:8083"]
}

# Restart Docker
sudo systemctl restart docker
```

#### Option B: Use HTTPS (Recommended for Production)

If Nexus is configured with SSL/TLS, use HTTPS instead:
- Registry URL: `https://10.2.0.11:8443` (or your HTTPS port)
- No insecure registry configuration needed

### Step 3: Login to Nexus Docker Registry

```bash
# Login to Nexus Docker registry
docker login 10.2.0.11:8083

# Enter your Nexus username and password when prompted
```

**Note**: Use the same credentials you use for Nexus Maven repositories.

### Step 4: Tag Your Image Correctly

The image tag format for Nexus Docker registry is:
```
<nexus-host>:<port>/<repository-name>/<image-name>:<tag>
```

**Example**:
```bash
# Tag your image
docker tag geological-sample-api:latest 10.2.0.11:8083/docker-hosted/geological-sample-api:v1

# Or if repository name matches image name:
docker tag geological-sample-api:latest 10.2.0.11:8083/geological-sample-api:v1
```

**Important**: The repository name in the tag should match the Docker repository name you created in Nexus.

### Step 5: Push the Image

```bash
# Push to Nexus
docker push 10.2.0.11:8083/docker-hosted/geological-sample-api:v1

# Or if using repository name directly:
docker push 10.2.0.11:8083/geological-sample-api:v1
```

## Complete Workflow Example

```bash
# 1. Build the image
docker build -t geological-sample-api:latest .

# 2. Tag for Nexus
docker tag geological-sample-api:latest 10.2.0.11:8083/docker-hosted/geological-sample-api:v1

# 3. Login to Nexus
docker login 10.2.0.11:8083

# 4. Push to Nexus
docker push 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
```

## Troubleshooting

### Error: "repository name must be lowercase"

Docker repository names must be lowercase. Use:
```bash
docker tag geological-sample-api:latest 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
```

### Error: "unauthorized: authentication required"

1. **Check login**:
   ```bash
   docker login 10.2.0.11:8083
   ```

2. **Verify Nexus permissions**: Ensure your user has:
   - **nx-repository-view-docker-docker-hosted-read**
   - **nx-repository-view-docker-docker-hosted-write**

3. **Check Nexus Realms**: 
   - Go to Nexus → **Administration** → **Security** → **Realms**
   - Ensure **Docker Bearer Token Realm** is active

### Error: "connection refused" or "cannot connect"

1. **Verify Nexus is running**:
   ```bash
   curl http://10.2.0.11:8083/v2/
   ```

2. **Check firewall**: Ensure port 8083 is open

3. **Verify repository exists**: Check Nexus UI → Repositories

### Error: "404 Not Found"

This is the error you're experiencing. Solutions:

1. **Repository doesn't exist**: Create Docker hosted repository (Step 1)
2. **Wrong repository name**: Use exact name from Nexus
3. **Wrong port**: Verify Docker registry port in Nexus settings
4. **Repository not started**: Check repository status in Nexus UI

## Alternative: Using Docker Hub or Other Registries

If Nexus Docker registry setup is complex, you can use:

### Docker Hub
```bash
docker tag geological-sample-api:latest yourusername/geological-sample-api:v1
docker push yourusername/geological-sample-api:v1
```

### Private Registry (Harbor, GitLab Registry, etc.)
```bash
docker tag geological-sample-api:latest registry.example.com/geological-sample-api:v1
docker push registry.example.com/geological-sample-api:v1
```

## Verification

After successful push, verify in Nexus:

1. Go to Nexus UI → **Browse** → Select your Docker repository
2. You should see your image: `geological-sample-api:v1`

Or via API:
```bash
curl -u username:password http://10.2.0.11:8083/v2/geological-sample-api/tags/list
```

## Pulling Images

To pull the image later:
```bash
docker pull 10.2.0.11:8083/docker-hosted/geological-sample-api:v1
```

## Nexus Repository Manager 3 Configuration

If using Nexus Repository Manager 3, the Docker registry setup is:

1. **Repository Format**: docker (hosted)
2. **Name**: `docker-hosted` (or your preferred name)
3. **HTTP Port**: `8083` (or your preferred port)
4. **Enable Docker V1 API**: Yes (for compatibility)
5. **Blob Store**: Choose or create a blob store

## Security Best Practices

1. **Use HTTPS**: Configure SSL/TLS for production
2. **Authentication**: Always require authentication
3. **Permissions**: Use role-based access control
4. **Image Scanning**: Enable vulnerability scanning if available
5. **Cleanup Policies**: Set up retention policies for old images

## Additional Resources

- [Nexus Docker Registry Documentation](https://help.sonatype.com/repomanager3/formats/docker-registry)
- [Docker Registry API](https://docs.docker.com/registry/spec/api/)
- [Docker Insecure Registry Configuration](https://docs.docker.com/registry/insecure/)
