#!/usr/bin/env bash
set -euo pipefail

INFRA_DIR="/opt/monitoring"
REPO_INFRA="$(cd "$(dirname "$0")" && pwd)"

echo "=== AgiBank QA - Monitoring Stack Setup ==="
echo "Source: $REPO_INFRA"
echo "Target: $INFRA_DIR"
echo ""

if ! command -v docker &> /dev/null; then
  echo "Installing Docker..."
  curl -fsSL https://get.docker.com | sh
  sudo usermod -aG docker "$USER"
  echo "Docker installed. You may need to log out and back in for group changes."
fi

if ! docker compose version &> /dev/null; then
  echo "ERROR: docker compose plugin not found."
  echo "Install it: sudo apt install docker-compose-plugin"
  exit 1
fi

sudo mkdir -p "$INFRA_DIR"
sudo chown -R "$USER:$USER" "$INFRA_DIR"

echo "Syncing infra files to $INFRA_DIR..."
rsync -avz --delete \
  --exclude='setup-vps.sh' \
  --exclude='scripts/' \
  "$REPO_INFRA/" "$INFRA_DIR/"

echo ""
echo "Starting monitoring stack..."
cd "$INFRA_DIR"
docker compose up -d

echo ""
echo "Waiting for services to be healthy..."
sleep 10

echo ""
echo "=== Service Status ==="
docker compose ps

echo ""
echo "=== Health Checks ==="
echo -n "InfluxDB: "
curl -sf http://localhost:8086/ping && echo "OK" || echo "FAIL"
echo -n "Prometheus: "
curl -sf http://localhost:9090/-/healthy && echo "OK" || echo "FAIL"
echo -n "Grafana: "
curl -sf http://localhost:3000/api/health | python3 -m json.tool 2>/dev/null || echo "FAIL"
echo -n "Node Exporter: "
curl -sf http://localhost:9100/metrics | head -1 && echo "" || echo "FAIL"

echo ""
echo "=== Setup Complete ==="
echo "Grafana: http://localhost:3000 (admin / agibank2024)"
echo "InfluxDB: http://localhost:8086"
echo "Prometheus: http://localhost:9090"
echo ""
echo "Next: configure Nginx reverse proxy for /grafana/"
