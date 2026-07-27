#!/usr/bin/env bash
set -euo pipefail

if [ -z "${GROQ_API_KEY:-}" ]; then
  echo "ERROR: GROQ_API_KEY is required"
  echo "Usage: GROQ_API_KEY=\"gsk_...\" $0"
  exit 1
fi

NET="ats-net"

echo "=== Creating network ==="
docker network create "$NET" 2>/dev/null || true

echo "=== Starting PostgreSQL ==="
docker run -d --network "$NET" --name ats-db \
  -e POSTGRES_DB=ats -e POSTGRES_USER=ats -e POSTGRES_PASSWORD=ats \
  dhi.io/postgres:18-alpine3.22-dev

echo "=== Waiting for PostgreSQL to be ready ==="
until docker exec ats-db pg_isready -U ats >/dev/null 2>&1; do
  sleep 1
done
echo "PostgreSQL is ready"

echo "=== Building and starting backend ==="
docker build -f Dockerfile.backend -t ats-backend .
docker run -d --network "$NET" --name ats-backend -p 8080:8080 \
  -e GROQ_API_KEY="$GROQ_API_KEY" \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=ats-db \
  -e DB_USER=ats \
  -e DB_PASSWORD=ats \
  ats-backend

echo "=== Building and starting frontend ==="
docker build -f Dockerfile.frontend -t ats-frontend .
docker run -d --network "$NET" --name ats-frontend -p 80:80 \
  ats-frontend

echo ""
echo "=== Stack is running ==="
echo "  Frontend: http://localhost:80"
echo "  Backend:  http://localhost:8080"
echo ""
echo "To stop: docker stop ats-frontend ats-backend ats-db && docker rm ats-frontend ats-backend ats-db"
