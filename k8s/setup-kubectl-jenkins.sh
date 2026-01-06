#!/bin/bash
# Script to configure kubectl on Jenkins server for Kubernetes deployment
# Run this script on the Jenkins server

set -e

echo "=========================================="
echo "Configuring kubectl for Jenkins"
echo "=========================================="

# Check if running as root or with sudo
if [ "$EUID" -ne 0 ]; then 
    echo "Please run with sudo"
    exit 1
fi

# Install kubectl if not present
if ! command -v kubectl &> /dev/null; then
    echo "Installing kubectl..."
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
    chmod +x kubectl
    mv kubectl /usr/local/bin/
    echo "✓ kubectl installed"
else
    echo "✓ kubectl already installed"
    kubectl version --client
fi

# Create .kube directory for Jenkins user
echo "Creating .kube directory for Jenkins user..."
mkdir -p /var/lib/jenkins/.kube
chown jenkins:jenkins /var/lib/jenkins/.kube
chmod 755 /var/lib/jenkins/.kube

echo ""
echo "=========================================="
echo "Next Steps:"
echo "=========================================="
echo "1. Copy kubeconfig from master node:"
echo "   On master node (10.2.0.15), run:"
echo "   sudo cat /etc/kubernetes/admin.conf"
echo ""
echo "2. On Jenkins server, create kubeconfig:"
echo "   sudo nano /var/lib/jenkins/.kube/config"
echo "   (Paste the kubeconfig content)"
echo ""
echo "3. Set permissions:"
echo "   sudo chown jenkins:jenkins /var/lib/jenkins/.kube/config"
echo "   sudo chmod 600 /var/lib/jenkins/.kube/config"
echo ""
echo "4. Test access:"
echo "   sudo -u jenkins kubectl get nodes"
echo "=========================================="
