# AgiBank QA Automation Challenge

[![CI](https://github.com/rennangimenez/agibank-qa-automation-challenge/actions/workflows/ci.yml/badge.svg)](https://github.com/rennangimenez/agibank-qa-automation-challenge/actions/workflows/ci.yml)

End-to-end QA automation project covering **Web**, **API**, and **Performance** testing using Java 17, Playwright, RestAssured, and JMeter. Includes **security testing**, **observability** with Grafana, and a full CI/CD pipeline.

**Live Reports:** [rennangimenez.com/agibank-challenge](https://rennangimenez.com/agibank-challenge/)
**Grafana Dashboards:** [rennangimenez.com/grafana](https://rennangimenez.com/grafana/)

---

## Architecture

```
agibank-qa-automation-challenge/
├── web-tests/           Playwright Java — Blog do Agi (search, smoke, security)
├── api-tests/           RestAssured — Dog API (CRUD, edge cases, security)
├── performance-tests/   JMeter — BlazeDemo flight purchase (load + spike)
├── infra/               Grafana + InfluxDB + Prometheus (observability)
├── .github/workflows/   CI/CD pipelines
├── docs/                Test plan, test report, performance report
├── pom.xml              Parent POM (Maven multi-module)
└── package.json         Dev tooling (Husky, Prettier)
```

### Tech Stack

| Layer         | Technology                      | Purpose                               |
| ------------- | ------------------------------- | ------------------------------------- |
| Language      | Java 17                         | Core language                         |
| Build         | Maven 3.9                       | Multi-module build with Maven Wrapper |
| Test Runner   | JUnit 5                         | Unified test execution                |
| Web           | Playwright Java                 | Browser automation with auto-waits    |
| API           | RestAssured                     | HTTP client with fluent assertions    |
| Performance   | JMeter 5.6                      | Load and spike testing                |
| Reports       | Allure                          | Interactive HTML reports (Web + API)  |
| Observability | Grafana + InfluxDB + Prometheus | Real-time dashboards and metrics      |
| CI/CD         | GitHub Actions                  | Self-hosted runner on VPS             |
| Code Quality  | Spotless + Husky + Prettier     | Formatting and pre-commit hooks       |

### Design Decisions

- **Playwright over Selenium**: Auto-waits reduce flakiness; modern API with better DX; built-in support for multiple browser contexts.
- **RestAssured with Client Layer**: Separates HTTP logic from test assertions following SRP; `DogApiClient` encapsulates all API calls, making tests readable and maintainable.
- **JSON Schema Validation**: Validates API contract structure beyond just status codes, catching breaking changes early.
- **Maven Multi-Module**: Each test type is an independent module with its own dependencies, avoiding classpath conflicts while sharing common configuration through the parent POM.
- **Grafana + InfluxDB**: JMeter sends real-time metrics via Backend Listener; CI pushes Allure results and pipeline metrics to InfluxDB, enabling 3 focused dashboards (Quality, Performance, Pipeline Health).
- **Security-First Approach**: Security tests document real findings (HTML injection, info disclosure) as positive assertions, demonstrating vulnerability detection capability.

---

## Test Coverage Summary

**Total: 42 test scenarios** (14 Web + 26 API + 2 Performance plans)

| Module              | Tests  | Categories                                                |
| ------------------- | ------ | --------------------------------------------------------- |
| Web - Search        | 3      | Functional                                                |
| Web - Smoke         | 6      | Smoke                                                     |
| Web - Security      | 5      | Security (SQL Injection, XSS, HTML Injection)             |
| API - Breed List    | 4      | Functional + Contract                                     |
| API - Breed Images  | 4      | Functional                                                |
| API - Random Image  | 3      | Functional + Contract                                     |
| API - Edge Cases    | 9      | Edge Cases + Functional                                   |
| API - Security      | 6      | Security (SQL Injection, Path Traversal, Info Disclosure) |
| Performance - Load  | 1 plan | Load testing (150 threads, 240s)                          |
| Performance - Spike | 1 plan | Spike testing (3 phases, 200 threads peak)                |

### Security Findings

| ID      | Finding                                                        | Severity | Application |
| ------- | -------------------------------------------------------------- | -------- | ----------- |
| WEB-012 | HTML Injection renders as actual DOM element in search results | Critical | Blog do Agi |
| API-023 | X-Powered-By header exposes PHP/8.3.29                         | Normal   | Dog API     |

---

## Prerequisites

- **Java 17+** (JDK, not just JRE)
- **Node.js 18+** (for dev tooling: Husky, Prettier)
- **Git**

Maven is handled automatically via the included Maven Wrapper (`mvnw`).

---

## Setup

```bash
# Clone
git clone https://github.com/rennangimenez/agibank-qa-automation-challenge.git
cd agibank-qa-automation-challenge

# Install dev tooling (Husky + Prettier)
npm install

# Install Playwright browsers (required for web tests)
./mvnw exec:java -pl web-tests -e \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="install --with-deps chromium"
```

---

## Running Tests

### API Tests (26 tests)

```bash
./mvnw clean test -pl api-tests
```

Tests the [Dog API](https://dog.ceo/dog-api/documentation):

- Breed list, images, and random image endpoints
- JSON Schema contract validation
- Edge cases: special characters, unicode, boundary values
- Security: SQL injection, path traversal, XSS reflection, header analysis

### Web Tests (14 tests)

```bash
./mvnw clean test -pl web-tests
```

Tests the [Blog do Agi](https://blogdoagi.com.br/):

- Search with valid/invalid terms
- Smoke tests (page load, navigation, articles, footer)
- Security: SQL injection, XSS execution, HTML injection, long input handling

### Performance Tests

```bash
# Load test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-load-test

# Spike test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-spike-test
```

Tests the [BlazeDemo](https://www.blazedemo.com) flight purchase flow:

- **Load Test**: Ramp to 150 threads over 60s, sustain for 240s, targeting 250 req/s
- **Spike Test**: 3 phases -- warm-up (30 threads) -> spike (200 threads) -> cool-down (30 threads)

### Run All (API + Web)

```bash
./mvnw clean test -pl api-tests,web-tests
```

---

## Reports & Observability

### Live Reports

Reports are automatically deployed to the VPS on every CI run:

| Report               | URL                                                                                                          |
| -------------------- | ------------------------------------------------------------------------------------------------------------ |
| Web Tests (Allure)   | [rennangimenez.com/agibank-challenge/web/](https://rennangimenez.com/agibank-challenge/web/)                 |
| API Tests (Allure)   | [rennangimenez.com/agibank-challenge/api/](https://rennangimenez.com/agibank-challenge/api/)                 |
| Performance (JMeter) | [rennangimenez.com/agibank-challenge/performance/](https://rennangimenez.com/agibank-challenge/performance/) |
| Grafana Dashboards   | [rennangimenez.com/grafana/](https://rennangimenez.com/grafana/)                                             |

### Grafana Dashboards

| Dashboard                  | Purpose                                                                               |
| -------------------------- | ------------------------------------------------------------------------------------- |
| Quality Overview           | Web/API test results separated, pass rates, trends, flaky test tracking               |
| Performance (JMeter)       | Real-time latency (avg/p90/p95/p99), throughput, error rate, per-transaction analysis |
| Pipeline & Delivery Health | CI/CD success rate, job durations, feedback time, failure analysis                    |

### Local Reports

```bash
# Allure
./mvnw allure:serve -pl api-tests
./mvnw allure:serve -pl web-tests

# JMeter
# Report generated at: performance-tests/target/jmeter/reports/
```

---

## Observability Stack

The project includes a full monitoring stack deployed on the VPS via Docker Compose:

```
infra/
├── docker-compose.yml                Grafana + InfluxDB + Prometheus
├── prometheus/prometheus.yml          Prometheus scrape config
├── grafana/
│   ├── provisioning/                  Auto-configured datasources and dashboard providers
│   └── dashboards/
│       ├── quality-overview.json      Web/API test results, pass rates, flaky tracking
│       ├── performance.json           JMeter latency, throughput, error analysis
│       └── pipeline-health.json       CI/CD success rate, job durations, feedback time
└── scripts/
    ├── push-allure-metrics.sh         Pushes test results + stability metrics to InfluxDB
    └── push-pipeline-metrics.sh       Pushes CI job duration + status to InfluxDB
```

**Data flow:**

- JMeter sends real-time metrics to InfluxDB via Backend Listener during execution
- CI workflows push Allure test results (pass/fail/flaky) after each run
- CI workflows push pipeline metrics (duration, status) for delivery health tracking
- Dashboards are provisioned as code and auto-deployed via CI

---

## CI/CD

### Workflows

| Workflow          | Trigger                    | What it does                                                                                    |
| ----------------- | -------------------------- | ----------------------------------------------------------------------------------------------- |
| `ci.yml`          | Push to `main`, PRs        | Deploys monitoring stack, runs API + Web tests, deploys reports, pushes test + pipeline metrics |
| `performance.yml` | Manual (workflow_dispatch) | Runs selected JMeter test plan, deploys HTML report, pushes pipeline metrics                    |

### Infrastructure

- **Runner**: Self-hosted GitHub Actions runner on VPS (`agibank-vps`)
- **Reports**: Deployed via `rsync` to `/srv/apps/agibank-challenge-reports/`
- **Monitoring**: Grafana at `rennangimenez.com/grafana/`
- **Served by**: Nginx at `rennangimenez.com/agibank-challenge/`

---

## Code Quality

- **Java formatting**: Google Java Format via [Spotless](https://github.com/diffplug/spotless) (`./mvnw spotless:apply`)
- **File formatting**: [Prettier](https://prettier.io/) for YAML, JSON, Markdown
- **Pre-commit hooks**: [Husky](https://typicode.github.io/husky/) runs lint-staged + Spotless check before every commit
- **EditorConfig**: Consistent indentation across editors

---

## Documentation

- [Test Plan](docs/test-plan.md) -- Complete test plan with all 42 scenarios and 25 proposed next steps
- [Test Report (Web + API)](docs/test-report.md) -- Execution results for 14 Web + 26 API tests
- [Performance Report](docs/performance-report.md) -- Load and spike test results on BlazeDemo

---

## Project Structure

```
├── api-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/br/com/agibank/qa/api/
│       │   ├── client/
│       │   │   └── DogApiClient.java          # HTTP client layer
│       │   ├── fixtures/
│       │   │   ├── BreedData.java             # Test data: breeds, URLs, counts
│       │   │   └── SecurityPayloads.java      # Test data: SQL injection, XSS, etc.
│       │   └── tests/
│       │       ├── BreedListTest.java          # /breeds/list/all
│       │       ├── BreedImagesTest.java        # /breed/{breed}/images
│       │       ├── RandomImageTest.java        # /breeds/image/random
│       │       ├── EdgeCaseTest.java           # Edge cases & boundary values
│       │       └── SecurityTest.java           # SQL injection, path traversal
│       └── resources/schemas/                  # JSON Schema files
│
├── web-tests/
│   ├── pom.xml
│   └── src/test/java/br/com/agibank/qa/web/
│       ├── base/
│       │   └── BaseTest.java                   # Playwright lifecycle
│       ├── pages/
│       │   ├── BlogHomePage.java               # Home page actions
│       │   └── SearchResultsPage.java          # Results page assertions
│       ├── fixtures/
│       │   ├── SearchData.java                 # Test data: search terms, payloads
│       │   └── ExpectedResults.java            # Test data: URLs, selectors
│       └── tests/
│           ├── BlogSearchTest.java             # Search scenarios
│           ├── BlogSmokeTest.java              # Smoke tests
│           └── BlogSecurityTest.java           # Security tests
│
├── performance-tests/
│   ├── pom.xml
│   └── src/test/jmeter/
│       ├── blazedemo-load-test.jmx             # Sustained load test
│       └── blazedemo-spike-test.jmx            # Spike/burst test
│
├── infra/                                      # Monitoring stack
│   ├── docker-compose.yml                      # Grafana + InfluxDB + Prometheus
│   ├── grafana/dashboards/                     # 3 provisioned dashboard JSONs
│   └── scripts/
│       ├── push-allure-metrics.sh              # Test results + flaky metrics to InfluxDB
│       └── push-pipeline-metrics.sh            # CI/CD job duration + status to InfluxDB
│
├── .github/workflows/
│   ├── ci.yml                                  # API + Web CI with metrics push
│   └── performance.yml                         # Manual perf trigger
│
├── docs/
│   ├── test-plan.md                            # Complete test plan (42 scenarios)
│   ├── test-report.md                          # Web + API execution results
│   └── performance-report.md                   # Performance test results
│
├── pom.xml                                     # Parent POM
├── mvnw / mvnw.cmd                             # Maven Wrapper
├── package.json                                # Husky + Prettier
└── README.md
```

---

## Author

**Rennan Gimenez** -- [GitHub](https://github.com/rennangimenez) | [Portfolio](https://rennangimenez.com)
