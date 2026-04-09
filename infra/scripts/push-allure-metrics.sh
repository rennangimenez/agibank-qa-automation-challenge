#!/usr/bin/env bash
set -euo pipefail

INFLUXDB_URL="${INFLUXDB_URL:-http://localhost:8086}"
INFLUXDB_DB="${INFLUXDB_DB:-jmeter}"

usage() {
  echo "Usage: $0 <allure-report-dir> <test-type> [branch] [commit-sha]"
  echo ""
  echo "  allure-report-dir  Path to generated Allure report (contains widgets/)"
  echo "  test-type          Tag value: web, api, etc."
  echo "  branch             Git branch (default: current branch)"
  echo "  commit-sha         Git commit SHA (default: current HEAD)"
  exit 1
}

if [ $# -lt 2 ]; then
  usage
fi

REPORT_DIR="$1"
TEST_TYPE="$2"
BRANCH="${3:-$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo 'unknown')}"
COMMIT_SHA="${4:-$(git rev-parse --short HEAD 2>/dev/null || echo 'unknown')}"

SUMMARY_FILE="$REPORT_DIR/widgets/summary.json"

if [ ! -f "$SUMMARY_FILE" ]; then
  echo "ERROR: $SUMMARY_FILE not found"
  echo "Make sure the Allure report has been generated first."
  exit 1
fi

TOTAL=$(python3 -c "import json; d=json.load(open('$SUMMARY_FILE')); print(d['statistic']['total'])")
PASSED=$(python3 -c "import json; d=json.load(open('$SUMMARY_FILE')); print(d['statistic']['passed'])")
FAILED=$(python3 -c "import json; d=json.load(open('$SUMMARY_FILE')); print(d['statistic']['failed'])")
BROKEN=$(python3 -c "import json; d=json.load(open('$SUMMARY_FILE')); print(d['statistic']['broken'])")
SKIPPED=$(python3 -c "import json; d=json.load(open('$SUMMARY_FILE')); print(d['statistic']['skipped'])")
DURATION=$(python3 -c "import json; d=json.load(open('$SUMMARY_FILE')); print(d['time'].get('duration', 0))")

TIMESTAMP=$(date +%s%N)

LINE="test_results,test_type=${TEST_TYPE},branch=${BRANCH},commit=${COMMIT_SHA} total=${TOTAL}i,passed=${PASSED}i,failed=${FAILED}i,broken=${BROKEN}i,skipped=${SKIPPED}i,duration=${DURATION}i ${TIMESTAMP}"

echo "Pushing Allure metrics to InfluxDB..."
echo "  Test Type: $TEST_TYPE"
echo "  Branch: $BRANCH"
echo "  Commit: $COMMIT_SHA"
echo "  Results: total=$TOTAL passed=$PASSED failed=$FAILED broken=$BROKEN skipped=$SKIPPED"
echo "  Duration: ${DURATION}ms"

RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -XPOST "${INFLUXDB_URL}/write?db=${INFLUXDB_DB}" \
  --data-binary "$LINE")

if [ "$RESPONSE" = "204" ]; then
  echo "Metrics pushed successfully."
else
  echo "WARNING: InfluxDB returned HTTP $RESPONSE (expected 204)"
  echo "Data: $LINE"
  exit 1
fi
