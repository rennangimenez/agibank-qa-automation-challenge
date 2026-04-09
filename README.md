# AgiBank QA Automation Challenge

[![CI](https://github.com/rennangimenez/agibank-qa-automation-challenge/actions/workflows/ci.yml/badge.svg)](https://github.com/rennangimenez/agibank-qa-automation-challenge/actions/workflows/ci.yml)

End-to-end QA automation project covering **Web**, **API**, and **Performance** testing using Java 17, Playwright, RestAssured, and JMeter.

**Live Reports:** [rennangimenez.com/agibank-challenge](https://rennangimenez.com/agibank-challenge/)

---

## Architecture

```
agibank-qa-automation-challenge/
├── web-tests/           Playwright Java — Blog do Agi search
├── api-tests/           RestAssured — Dog API endpoints
├── performance-tests/   JMeter — BlazeDemo flight purchase
├── .github/workflows/   CI/CD pipelines
├── pom.xml              Parent POM (Maven multi-module)
└── package.json         Dev tooling (Husky, Prettier)
```

### Tech Stack

| Layer        | Technology                  | Purpose                               |
| ------------ | --------------------------- | ------------------------------------- |
| Language     | Java 17                     | Core language                         |
| Build        | Maven 3.9                   | Multi-module build with Maven Wrapper |
| Test Runner  | JUnit 5                     | Unified test execution                |
| Web          | Playwright Java             | Browser automation with auto-waits    |
| API          | RestAssured                 | HTTP client with fluent assertions    |
| Performance  | JMeter 5.6                  | Load and spike testing                |
| Reports      | Allure                      | Interactive HTML reports (Web + API)  |
| CI/CD        | GitHub Actions              | Self-hosted runner on VPS             |
| Code Quality | Spotless + Husky + Prettier | Formatting and pre-commit hooks       |

### Design Decisions

- **Playwright over Selenium**: Auto-waits reduce flakiness; modern API with better DX; built-in support for multiple browser contexts.
- **RestAssured with Client Layer**: Separates HTTP logic from test assertions following SRP; `DogApiClient` encapsulates all API calls, making tests readable and maintainable.
- **JSON Schema Validation**: Validates API contract structure beyond just status codes, catching breaking changes early.
- **Maven Multi-Module**: Each test type is an independent module with its own dependencies, avoiding classpath conflicts while sharing common configuration through the parent POM.
- **Allure Reports**: Rich, interactive reports with step-by-step execution details, attachments, and history trends.

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

### API Tests

```bash
./mvnw clean test -pl api-tests
```

Tests the [Dog API](https://dog.ceo/dog-api/documentation) endpoints:

- `GET /breeds/list/all` — List all breeds with sub-breeds
- `GET /breed/{breed}/images` — Get images for a specific breed
- `GET /breeds/image/random` — Get a random dog image

### Web Tests

```bash
./mvnw clean test -pl web-tests
```

Tests the [Blog do Agi](https://blogdoagi.com.br/) search functionality:

- Search with valid term returns results
- Search with nonexistent term shows no-results message
- Search icon visibility and functionality

### Performance Tests

```bash
# Load test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-load-test

# Spike test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-spike-test
```

Tests the [BlazeDemo](https://www.blazedemo.com) flight purchase flow under load:

- **Load Test**: Ramp to 150 threads over 60s, sustain for 240s, targeting 250 req/s via Constant Throughput Timer
- **Spike Test**: 3 phases — base load (30 threads, 60s) → spike (200 threads in 5s, 120s) → cool-down (30 threads, 60s)

### Run All (API + Web)

```bash
./mvnw clean test -pl api-tests,web-tests
```

---

## Reports

### Allure Reports (local)

```bash
# Generate and open in browser
./mvnw allure:serve -pl api-tests
./mvnw allure:serve -pl web-tests
```

### Live Reports

Reports are automatically deployed to the VPS on every CI run:

- **Web Tests**: [rennangimenez.com/agibank-challenge/web/](https://rennangimenez.com/agibank-challenge/web/)
- **API Tests**: [rennangimenez.com/agibank-challenge/api/](https://rennangimenez.com/agibank-challenge/api/)
- **Performance**: [rennangimenez.com/agibank-challenge/performance/](https://rennangimenez.com/agibank-challenge/performance/)

### JMeter Reports (local)

After running performance tests, the HTML report is generated at:

```
performance-tests/target/jmeter/reports/
```

---

## Performance Analysis

### Acceptance Criteria

- **Throughput**: 250 requests per second
- **Response Time**: 90th percentile below 2 seconds

### Test Scenario

Full flight purchase flow on BlazeDemo:

1. `GET /` — Home page
2. `POST /reserve.php` — Search flights (Philadelphia → Buenos Aires)
3. `POST /purchase.php` — Select flight
4. `POST /confirmation.php` — Complete purchase

### Results

> Results will be populated after the first execution. See the [live performance report](https://rennangimenez.com/agibank-challenge/performance/) for detailed metrics.

---

## CI/CD

### Workflows

| Workflow          | Trigger                    | What it does                                        |
| ----------------- | -------------------------- | --------------------------------------------------- |
| `ci.yml`          | Push to `main`, PRs        | Runs API + Web tests, deploys Allure reports to VPS |
| `performance.yml` | Manual (workflow_dispatch) | Runs selected JMeter test plan, deploys HTML report |

### Infrastructure

- **Runner**: Self-hosted GitHub Actions runner on VPS (`agibank-vps`)
- **Reports**: Deployed via `rsync` to `/srv/apps/agibank-challenge-reports/`
- **Served by**: Nginx at `rennangimenez.com/agibank-challenge/`

---

## Code Quality

- **Java formatting**: Google Java Format via [Spotless](https://github.com/diffplug/spotless) (`./mvnw spotless:apply`)
- **File formatting**: [Prettier](https://prettier.io/) for YAML, JSON, Markdown
- **Pre-commit hooks**: [Husky](https://typicode.github.io/husky/) runs lint-staged + Spotless check before every commit
- **EditorConfig**: Consistent indentation across editors

---

## Project Structure

```
├── api-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/br/com/agibank/qa/api/
│       │   ├── client/
│       │   │   └── DogApiClient.java        # HTTP client layer
│       │   └── tests/
│       │       ├── BreedListTest.java        # /breeds/list/all
│       │       ├── BreedImagesTest.java      # /breed/{breed}/images
│       │       └── RandomImageTest.java      # /breeds/image/random
│       └── resources/schemas/                # JSON Schema files
│
├── web-tests/
│   ├── pom.xml
│   └── src/test/java/br/com/agibank/qa/web/
│       ├── base/
│       │   └── BaseTest.java                 # Playwright lifecycle
│       ├── pages/
│       │   ├── BlogHomePage.java             # Home page actions
│       │   └── SearchResultsPage.java        # Results page assertions
│       └── tests/
│           └── BlogSearchTest.java           # Search scenarios
│
├── performance-tests/
│   ├── pom.xml
│   └── src/test/jmeter/
│       ├── blazedemo-load-test.jmx           # Sustained load test
│       └── blazedemo-spike-test.jmx          # Spike/burst test
│
├── .github/workflows/
│   ├── ci.yml                                # API + Web CI
│   └── performance.yml                       # Manual perf trigger
│
├── pom.xml                                   # Parent POM
├── mvnw / mvnw.cmd                           # Maven Wrapper
├── package.json                              # Husky + Prettier
└── README.md
```

---

## Author

**Rennan Gimenez** — [GitHub](https://github.com/rennangimenez) | [Portfolio](https://rennangimenez.com)
