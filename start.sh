#!/bin/bash

echo "=================================="
echo "User Management Service - Quick Start"
echo "=================================="
echo ""

if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "Step 1: Stopping any existing containers..."
docker-compose down

echo ""
echo "Step 2: Building and starting services..."
docker-compose up -d --build

echo ""
echo "Step 3: Waiting for services to be healthy..."
echo "This may take 30-60 seconds..."

MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:8080/api/v1/health > /dev/null 2>&1; then
        echo ""
        echo "✅ All services are up and running!"
        break
    fi

    echo -n "."
    sleep 2
    RETRY_COUNT=$((RETRY_COUNT + 1))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo ""
    echo "❌ Services did not start in time. Please check logs:"
    echo "   docker-compose logs -f app"
    exit 1
fi

echo ""
echo "=================================="
echo "🚀 User Management Service is ready!"
echo "=================================="
echo ""
echo "📚 API Documentation: http://localhost:8080/swagger-ui.html"
echo "🏥 Health Check:      http://localhost:8080/api/v1/health"
echo "📊 API Endpoints:     http://localhost:8080/api/v1/users"
echo ""
echo "=================================="
echo "Quick Test Commands:"
echo "=================================="
echo ""
echo "1. Health Check:"
echo "   curl http://localhost:8080/api/v1/health"
echo ""
echo "2. Get All Users:"
echo "   curl http://localhost:8080/api/v1/users"
echo ""
echo "3. Create User:"
echo '   curl -X POST http://localhost:8080/api/v1/users \'
echo '     -H "Content-Type: application/json" \'
echo '     -d '"'"'{"firstName":"Test","lastName":"User","email":"test@example.com","role":"USER"}'"'"
echo ""
echo "4. Get User Stats:"
echo "   curl http://localhost:8080/api/v1/users/stats"
echo ""
echo "=================================="
echo "Useful Commands:"
echo "=================================="
echo ""
echo "View logs:        docker-compose logs -f app"
echo "Stop services:    docker-compose down"
echo "Restart:          docker-compose restart"
echo "Rebuild:          docker-compose up -d --build"
echo ""
echo "=================================="
