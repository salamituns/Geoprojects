#!/bin/bash

# Script to build and deploy WAR to local Tomcat for testing
# Usage: ./run-local-tomcat.sh [tomcat-path]

set -e

# Default Tomcat path (adjust if needed)
TOMCAT_PATH="${1:-$CATALINA_HOME}"

if [ -z "$TOMCAT_PATH" ]; then
    echo "Error: Tomcat path not specified"
    echo "Usage: ./run-local-tomcat.sh [tomcat-path]"
    echo "Or set CATALINA_HOME environment variable"
    exit 1
fi

if [ ! -d "$TOMCAT_PATH/webapps" ]; then
    echo "Error: Tomcat webapps directory not found at $TOMCAT_PATH/webapps"
    exit 1
fi

echo "Building WAR file..."
mvn clean package

WAR_FILE="target/api.war"
if [ ! -f "$WAR_FILE" ]; then
    # Fallback to versioned name if finalName wasn't set
    WAR_FILE="target/geological-sample-api-1.0.0-SNAPSHOT.war"
    if [ ! -f "$WAR_FILE" ]; then
        echo "Error: WAR file not found in target/"
        exit 1
    fi
    echo "Found WAR: $WAR_FILE"
fi

echo "Deploying to Tomcat at $TOMCAT_PATH..."
cp "$WAR_FILE" "$TOMCAT_PATH/webapps/api.war"

echo "✅ WAR deployed successfully!"
echo ""
echo "Next steps:"
echo "1. Start Tomcat: $TOMCAT_PATH/bin/startup.sh"
echo "2. Access app at: http://localhost:8080/api/"
echo "3. Health check: http://localhost:8080/api/healthcheck"
echo "4. View logs: tail -f $TOMCAT_PATH/logs/catalina.out"

