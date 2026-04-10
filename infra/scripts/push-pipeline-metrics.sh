#!/usr/bin/env bash
set -euo pipefail

INFLUXDB_URL="${INFLUXDB_URL:-http://localhost:8086}"
INFLUXDB_DB="${INFLUXDB_DB:-jmeter}"

usage() {
  echo "Usage: $0 <job-name> <status> <duration-seconds> [branch] [commit-sha] [run-id]"
  echo ""
  echo "  job-name          CI job name (e.g. web-tests, api-tests, performance)"
  echo "  status            success | failure | cancelled"
  echo "  duration-seconds  Job wall-clock time in seconds"
  echo "  branch            Git branch (default: current)"
  echo "  commit-sha        Short commit SHA (default: current HEAD)"
  echo "  run-id            CI run identifier (optional)"
  exit 1
}

if [ $# -lt 3 ]; then
  usage
fi

JOB_NAME="$1"
STATUS="$2"
DURATION_SEC="$3"
BRANCH="${4:-$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo 'unknown')}"
COMMIT_SHA="${5:-$(git rev-parse --short HEAD 2>/dev/null || echo 'unknown')}"
RUN_ID="${6:-0}"

STATUS_CODE=0
[ "$STATUS" = "success" ] && STATUS_CODE=1
[ "$STATUS" = "failure" ] && STATUS_CODE=0
[ "$STATUS" = "cancelled" ] && STATUS_CODE=2

TIMESTAMP=$(date +%s%N)

LINES="pipeline_runs,job=${JOB_NAME},branch=${BRANCH},status=${STATUS},commit=${COMMIT_SHA} duration=${DURATION_SEC},status_code=${STATUS_CODE}i,run_id=\"${RUN_ID}\" ${TIMESTAMP}"

echo "=== Pipeline Metrics ==="
echo "  Job: $JOB_NAME  Status: $STATUS  Duration: ${DURATION_SEC}s"
echo "  Branch: $BRANCH  Commit: $COMMIT_SHA  Run: $RUN_ID"

RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -XPOST "${INFLUXDB_URL}/write?db=${INFLUXDB_DB}" \
  --data-binary "$LINES")

if [ "$RESPONSE" = "204" ]; then
  echo "Pipeline metrics pushed successfully."
else
  echo "WARNING: InfluxDB returned HTTP $RESPONSE"
  echo "Data: $LINES"
  exit 1
fi
