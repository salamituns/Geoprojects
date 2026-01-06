# Kubernetes Deployment Manifests

This directory contains Kubernetes manifests for deploying the Geological Sample API.

## Files

- `namespace.yaml` - Creates the namespace for the application
- `deployment.yaml` - Defines the application deployment
- `service.yaml` - Exposes the application via NodePort

## Prerequisites

1. Kubernetes cluster with kubectl configured
2. Image pull secret for Nexus Docker registry
3. Docker image pushed to Nexus registry

## Setup (One-time)

### Create Image Pull Secret

```bash
kubectl create secret docker-registry nexus-docker-registry-secret \
  --docker-server=10.2.0.11:8085 \
  --docker-username=salamituns \
  --docker-password=YOUR_NEXUS_PASSWORD \
  --namespace=geological-sample-api
```

### Apply Manifests

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## Access Application

After deployment, access the application via NodePort:

- Worker Node 1: `http://10.2.0.14:30080`
- Worker Node 2: `http://10.2.0.4:30080`
- Health Check: `http://10.2.0.14:30080/healthcheck`

## Useful Commands

```bash
# Check deployment status
kubectl get pods -n geological-sample-api
kubectl get svc -n geological-sample-api
kubectl get deployment -n geological-sample-api

# View logs
kubectl logs -n geological-sample-api -l app=geological-sample-api --tail=50

# Describe resources
kubectl describe deployment geological-sample-api -n geological-sample-api
kubectl describe pod <pod-name> -n geological-sample-api

# Scale deployment
kubectl scale deployment geological-sample-api --replicas=3 -n geological-sample-api

# Rollout status
kubectl rollout status deployment/geological-sample-api -n geological-sample-api
```
