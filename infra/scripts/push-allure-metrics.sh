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
  exit 1
fi

TIMESTAMP=$(date +%s%N)

read -r TOTAL PASSED FAILED BROKEN SKIPPED DURATION < <(python3 -c "
import json
d = json.load(open('$SUMMARY_FILE'))
s = d['statistic']
dur = d['time'].get('duration', 0)
print(s['total'], s['passed'], s['failed'], s['broken'], s['skipped'], dur)
")

PASS_RATE=0
if [ "$TOTAL" -gt 0 ]; then
  PASS_RATE=$(python3 -c "print(round($PASSED / $TOTAL * 100, 2))")
fi

SUITE_LINE="test_results,test_type=${TEST_TYPE},branch=${BRANCH},commit=${COMMIT_SHA} total=${TOTAL}i,passed=${PASSED}i,failed=${FAILED}i,broken=${BROKEN}i,skipped=${SKIPPED}i,duration=${DURATION}i,pass_rate=${PASS_RATE} ${TIMESTAMP}"

echo "=== Allure Metrics: ${TEST_TYPE} ==="
echo "  Results: total=$TOTAL passed=$PASSED failed=$FAILED broken=$BROKEN skipped=$SKIPPED"
echo "  Pass Rate: ${PASS_RATE}%  Duration: ${DURATION}ms"

LINES="$SUITE_LINE"

CASES_FILE="$REPORT_DIR/widgets/cases-trend.json"
if [ -f "$CASES_FILE" ]; then
  RETRIES=$(python3 -c "
import json
try:
    d = json.load(open('$CASES_FILE'))
    retried = sum(1 for item in d if item.get('data', {}).get('retry', 0) > 0) if isinstance(d, list) else 0
    print(retried)
except: print(0)
" 2>/dev/null || echo "0")

  FLAKY_RATE=0
  if [ "$TOTAL" -gt 0 ]; then
    FLAKY_RATE=$(python3 -c "print(round($RETRIES / $TOTAL * 100, 2))" 2>/dev/null || echo "0")
  fi

  FLAKY_LINE="test_stability,test_type=${TEST_TYPE},branch=${BRANCH} flaky_count=${RETRIES}i,flaky_rate=${FLAKY_RATE},total=${TOTAL}i ${TIMESTAMP}"
  LINES="${LINES}
${FLAKY_LINE}"
  echo "  Flaky: count=$RETRIES rate=${FLAKY_RATE}%"
fi

DURATION_FILE="$REPORT_DIR/widgets/duration-trend.json"
if [ -f "$DURATION_FILE" ]; then
  DURATION_DATA=$(python3 -c "
import json
try:
    d = json.load(open('$DURATION_FILE'))
    if isinstance(d, list) and len(d) > 0:
        entry = d[0].get('data', d[0]) if isinstance(d[0], dict) else {}
        duration = entry.get('duration', 0)
        print(duration)
    else: print(0)
except: print(0)
" 2>/dev/null || echo "0")

  if [ "$DURATION_DATA" != "0" ]; then
    DUR_LINE="test_duration_trend,test_type=${TEST_TYPE},branch=${BRANCH} duration=${DURATION_DATA}i ${TIMESTAMP}"
    LINES="${LINES}
${DUR_LINE}"
  fi
fi

SUITES_FILE="$REPORT_DIR/widgets/suites.json"
if [ -f "$SUITES_FILE" ]; then
  python3 -c "
import json, sys
try:
    d = json.load(open('$SUITES_FILE'))
    items = d.get('children', d) if isinstance(d, dict) else d
    if not isinstance(items, list): sys.exit(0)
    for suite in items:
        name = suite.get('name', 'unknown').replace(' ', '\\\\ ').replace(',', '\\\\,')
        stats = suite.get('statistic', {})
        t = stats.get('total', 0)
        p = stats.get('passed', 0)
        f = stats.get('failed', 0)
        b = stats.get('broken', 0)
        s = stats.get('skipped', 0)
        pr = round(p/t*100, 2) if t > 0 else 0
        print(f'test_suite,test_type=${TEST_TYPE},suite={name} total={t}i,passed={p}i,failed={f}i,broken={b}i,skipped={s}i,pass_rate={pr} ${TIMESTAMP}')
except: pass
" 2>/dev/null | while read -r line; do
    LINES="${LINES}
${line}"
  done
fi

echo ""
echo "Pushing to InfluxDB..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
  -XPOST "${INFLUXDB_URL}/write?db=${INFLUXDB_DB}" \
  --data-binary "$LINES")

if [ "$RESPONSE" = "204" ]; then
  echo "Metrics pushed successfully."
else
  echo "WARNING: InfluxDB returned HTTP $RESPONSE (expected 204)"
  echo "Data sent:"
  echo "$LINES"
  exit 1
fi
