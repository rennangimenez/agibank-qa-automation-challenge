<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/Playwright-1.44-2EAD33?style=for-the-badge&logo=playwright&logoColor=white" alt="Playwright" />
  <img src="https://img.shields.io/badge/RestAssured-5.4-4BAE4F?style=for-the-badge&logo=java&logoColor=white" alt="RestAssured" />
  <img src="https://img.shields.io/badge/JMeter-5.6-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white" alt="JMeter" />
  <img src="https://img.shields.io/badge/Allure-2.25-FF6347?style=for-the-badge&logo=qameta&logoColor=white" alt="Allure" />
  <img src="https://img.shields.io/badge/Grafana-Latest-F46800?style=for-the-badge&logo=grafana&logoColor=white" alt="Grafana" />
</p>

<h1 align="center">🏦 AgiBank QA Automation Challenge</h1>

<p align="center">
  <strong>Projeto completo de automação de testes cobrindo Web, API e Performance</strong><br />
  com testes de segurança ofensivos, observabilidade em tempo real e CI/CD automatizado.
</p>

<p align="center">
  <a href="https://github.com/rennangimenez/agibank-qa-automation-challenge/actions/workflows/ci.yml">
    <img src="https://github.com/rennangimenez/agibank-qa-automation-challenge/actions/workflows/ci.yml/badge.svg" alt="CI" />
  </a>
  <a href="https://rennangimenez.com/agibank-challenge/">
    <img src="https://img.shields.io/badge/📊_Reports-Online-brightgreen?style=flat-square" alt="Reports" />
  </a>
  <a href="https://rennangimenez.com/grafana/">
    <img src="https://img.shields.io/badge/📈_Grafana-Live-orange?style=flat-square" alt="Grafana" />
  </a>
</p>

---

## 🗂️ Índice

- [Visão Geral](#-visão-geral)
- [Links Rápidos](#-links-rápidos)
- [Arquitetura](#-arquitetura)
- [Stack Tecnológica](#-stack-tecnológica)
- [Decisões de Design](#-decisões-de-design)
- [Cobertura de Testes](#-cobertura-de-testes)
- [Findings de Segurança](#-findings-de-segurança)
- [Pré-requisitos](#-pré-requisitos)
- [Setup](#-setup)
- [Executando os Testes](#-executando-os-testes)
- [Reports e Evidências](#-reports-e-evidências)
- [Observabilidade](#-observabilidade)
- [CI/CD](#-cicd)
- [Qualidade de Código](#-qualidade-de-código)
- [Documentação](#-documentação)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Autor](#-autor)

---

## 🔭 Visão Geral

Este projeto é uma solução completa de automação de testes para o desafio técnico de **QA Senior** do AgiBank. Cobre **3 pilares** com foco em qualidade, segurança e observabilidade:

|       Pilar        | Aplicação                               | O que testa                                                        |    Testes    |
| :----------------: | :-------------------------------------- | :----------------------------------------------------------------- | :----------: |
|     🌐 **Web**     | [Blog do Agi](https://blogdoagi.com.br) | Busca, smoke tests, segurança (XSS, SQL Injection, HTML Injection) |    **14**    |
|     🐕 **API**     | [Dog API](https://dog.ceo/dog-api/)     | Endpoints, contrato JSON Schema, edge cases, segurança             |    **26**    |
| ✈️ **Performance** | [BlazeDemo](https://blazedemo.com)      | Carga sustentada (150 threads) e spike (200 threads pico)          | **2 planos** |

> **42 cenários automatizados** com execução programada 2x/dia, reports interativos e dashboards em tempo real.

---

## 🔗 Links Rápidos

<table>
  <tr>
    <td align="center">🌐<br /><strong><a href="https://rennangimenez.com/agibank-challenge/web/">Allure Web</a></strong><br /><sub>Report interativo</sub></td>
    <td align="center">🐕<br /><strong><a href="https://rennangimenez.com/agibank-challenge/api/">Allure API</a></strong><br /><sub>Report interativo</sub></td>
    <td align="center">✈️<br /><strong><a href="https://rennangimenez.com/agibank-challenge/performance/">JMeter</a></strong><br /><sub>HTML Report</sub></td>
    <td align="center">📈<br /><strong><a href="https://rennangimenez.com/grafana/">Grafana</a></strong><br /><sub>Dashboards live</sub></td>
    <td align="center">🔄<br /><strong><a href="https://github.com/rennangimenez/agibank-qa-automation-challenge/actions">Actions</a></strong><br /><sub>CI/CD Pipeline</sub></td>
  </tr>
</table>

---

## 🏗️ Arquitetura

```
agibank-qa-automation-challenge/
├── 🌐 web-tests/           Playwright Java — Blog do Agi (busca, smoke, segurança)
├── 🐕 api-tests/           RestAssured — Dog API (CRUD, edge cases, segurança)
├── ✈️ performance-tests/   JMeter — BlazeDemo (carga + spike)
├── 📊 infra/               Grafana + InfluxDB + Prometheus (observabilidade)
├── 🔄 .github/workflows/   CI/CD pipelines (self-hosted runner)
├── 📝 docs/                Plano de testes, relatórios
├── 📦 pom.xml              Parent POM (Maven multi-module)
└── 🧹 package.json         Ferramentas dev (Husky, Prettier)
```

### Fluxo de Dados

```
                    ┌──────────────┐
                    │ GitHub Push  │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │ GitHub Actions│
                    │ (self-hosted) │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼──┐  ┌──────▼──┐  ┌──────▼──────┐
       │API Tests│  │Web Tests│  │  Deploy      │
       │RestAssur│  │Playwrig │  │  Monitoring  │
       └────┬────┘  └────┬────┘  └──────┬──────┘
            │            │              │
       ┌────▼────────────▼────┐   ┌─────▼─────┐
       │   Allure Reports     │   │  Grafana   │
       │  (deploy via rsync)  │   │  InfluxDB  │
       └──────────────────────┘   └───────────┘
```

---

## 🛠️ Stack Tecnológica

|        Camada        | Tecnologia         | Versão | Para quê                            |
| :------------------: | :----------------- | :----: | :---------------------------------- |
|   💻 **Linguagem**   | Java               |   17   | Linguagem principal dos testes      |
|     📦 **Build**     | Maven              | 3.9.6  | Multi-module com Maven Wrapper      |
|  🧪 **Test Runner**  | JUnit 5            | 5.10.2 | Framework unificado de testes       |
|      🌐 **Web**      | Playwright Java    | 1.44.0 | Automação browser com auto-waits    |
|      🐕 **API**      | RestAssured        | 5.4.0  | HTTP client com assertions fluentes |
|  ✈️ **Performance**  | JMeter             | 5.6.3  | Testes de carga e spike             |
|   📊 **Reporting**   | Allure             | 2.25.0 | Reports interativos com evidências  |
| 📈 **Observability** | Grafana + InfluxDB | Latest | Dashboards e métricas tempo real    |
|     🔄 **CI/CD**     | GitHub Actions     |   —    | Self-hosted runner na VPS           |
| 🧹 **Code Quality**  | Spotless + Husky   |   —    | Formatação e pre-commit hooks       |

---

## 💡 Decisões de Design

<details>
<summary><strong>🎭 Por que Playwright e não Selenium?</strong></summary>

- **Auto-waits** embutidos reduzem flakiness drasticamente
- API moderna com melhor experiência de desenvolvimento
- Suporte nativo a múltiplos contextos de browser
- Tracing integrado para debug visual de falhas

</details>

<details>
<summary><strong>🔌 Por que RestAssured com camada Client?</strong></summary>

- Separa lógica HTTP das assertions seguindo SRP (Single Responsibility)
- `DogApiClient` encapsula todas as chamadas, tornando testes legíveis
- Facilita manutenção: mudança na API = mudança em 1 lugar

</details>

<details>
<summary><strong>📄 Por que JSON Schema Validation?</strong></summary>

- Valida a **estrutura** do contrato da API, não apenas status codes
- Detecta breaking changes antes que impactem consumidores
- Schemas versionados no repositório como fonte da verdade

</details>

<details>
<summary><strong>📦 Por que Maven Multi-Module?</strong></summary>

- Cada tipo de teste é um módulo independente com suas dependências
- Evita conflitos de classpath entre Playwright, RestAssured e JMeter
- Configurações comuns compartilhadas via Parent POM

</details>

<details>
<summary><strong>📈 Por que Grafana + InfluxDB?</strong></summary>

- JMeter envia métricas em tempo real via Backend Listener
- CI envia resultados Allure e métricas de pipeline para InfluxDB
- **3 dashboards especializados**: Quality, Performance e Pipeline Health
- Métricas cumulativas e tendências históricas

</details>

<details>
<summary><strong>🛡️ Por que testes de segurança documentam findings?</strong></summary>

- Testes que encontram vulnerabilidades são escritos como assertions positivas
- Documentam a **presença** da vulnerabilidade ao invés de falhar silenciosamente
- Demonstram capacidade real de detecção de vulnerabilidades
- Achados marcados com `⚠️ [ACHADO]` no Allure para visibilidade

</details>

---

## 🧪 Cobertura de Testes

**Total: 42 cenários** — 14 Web + 26 API + 2 planos de Performance

### 🌐 Web Tests — Blog do Agi (14 testes)

| Suite              | Testes | Cobertura                                                             |
| :----------------- | :----: | :-------------------------------------------------------------------- |
| 💨 **Smoke Tests** |   6    | Carregamento, navegação, logo, footer, artigos, JS errors             |
| 🔍 **Busca**       |   3    | Busca válida, termo inexistente, componentes UI                       |
| 🛡️ **Segurança**   |   5    | SQL Injection, XSS, HTML Injection, input longo, caracteres especiais |

### 🐕 API Tests — Dog API (26 testes)

| Suite                    | Testes | Cobertura                                                               |
| :----------------------- | :----: | :---------------------------------------------------------------------- |
| 📋 **Listagem de Raças** |   4    | GET all breeds, raças conhecidas, sub-raças, JSON Schema                |
| 🖼️ **Imagens por Raça**  |   4    | Imagens válidas, URLs CDN, raça inválida 404                            |
| 🎲 **Imagem Aleatória**  |   3    | Random image, extensão válida, JSON Schema                              |
| ⚠️ **Casos Extremos**    |   9    | Chars especiais, unicode, nomes longos, contagens negativas, sub-raças  |
| 🛡️ **Segurança**         |   6    | SQL Injection, Path Traversal, XSS, info disclosure, headers maliciosos |

### ✈️ Performance Tests — BlazeDemo (2 planos)

| Plano             | Config                                 | Fluxo                                     |
| :---------------- | :------------------------------------- | :---------------------------------------- |
| ⚡ **Load Test**  | 150 threads, ramp 60s, 240s sustentado | Home → Buscar voos → Escolher → Confirmar |
| 📈 **Spike Test** | 30 → 200 → 30 threads (3 fases)        | Mesmo fluxo com burst de tráfego          |

---

## 🔐 Findings de Segurança

> Vulnerabilidades reais encontradas e documentadas durante os testes.

### ⚠️ WEB-012 — HTML Injection no Blog do Agi

|                  |                                                                     |
| :--------------- | :------------------------------------------------------------------ |
| **Severidade**   | 🔴 Critical                                                         |
| **Tipo**         | HTML Injection (Reflected)                                          |
| **Vetor**        | Campo de busca → página de resultados                               |
| **Payload**      | `<h1>Injected</h1>`                                                 |
| **Impacto**      | Tag `<h1>` renderiza como elemento DOM real — potencial phishing    |
| **Recomendação** | Aplicar `esc_html()` no output do search term no template WordPress |

### ⚠️ API-023 — Information Disclosure na Dog API

|                  |                                                     |
| :--------------- | :-------------------------------------------------- |
| **Severidade**   | 🟡 Normal                                           |
| **Tipo**         | Information Disclosure                              |
| **Header**       | `X-Powered-By: PHP/8.3.29`                          |
| **Impacto**      | Expõe tecnologia e versão do backend para atacantes |
| **Recomendação** | `expose_php = Off` no `php.ini`                     |

---

## 📋 Pré-requisitos

| Requisito       | Versão   |          Obrigatório           |
| :-------------- | :------- | :----------------------------: |
| ☕ **Java JDK** | 17+      |               ✅               |
| 📗 **Node.js**  | 18+      |     ✅ (Husky + Prettier)      |
| 🔧 **Git**      | Qualquer |               ✅               |
| 📦 **Maven**    | —        | ❌ (incluso via Maven Wrapper) |

---

## 🚀 Setup

```bash
# 1️⃣ Clonar o repositório
git clone https://github.com/rennangimenez/agibank-qa-automation-challenge.git
cd agibank-qa-automation-challenge

# 2️⃣ Instalar ferramentas de dev (Husky + Prettier)
npm install

# 3️⃣ Instalar browsers do Playwright (necessário para testes web)
./mvnw exec:java -pl web-tests -e \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="install --with-deps chromium"
```

---

## ▶️ Executando os Testes

### 🐕 API Tests (26 testes)

```bash
./mvnw clean test -pl api-tests
```

Testa a [Dog API](https://dog.ceo/dog-api/documentation):

- 📋 Listagem de raças e sub-raças
- 📄 Validação de contrato JSON Schema
- ⚠️ Edge cases: caracteres especiais, unicode, limites
- 🛡️ Segurança: SQL Injection, Path Traversal, XSS, headers

### 🌐 Web Tests (14 testes)

```bash
./mvnw clean test -pl web-tests
```

Testa o [Blog do Agi](https://blogdoagi.com.br):

- 💨 Smoke: carregamento, navegação, artigos, JS errors
- 🔍 Busca: termos válidos/inválidos, componentes
- 🛡️ Segurança: SQL Injection, XSS, HTML Injection, input longo

### ✈️ Performance Tests

```bash
# ⚡ Load Test (150 threads, 240s)
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-load-test

# 📈 Spike Test (30 → 200 → 30 threads)
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-spike-test
```

### 🏃 Rodar Tudo (API + Web)

```bash
./mvnw clean test -pl api-tests,web-tests
```

---

## 📊 Reports e Evidências

### 🌍 Reports Online (atualizados a cada CI run)

| Report             | Link                                                                                                         | Formato           |
| :----------------- | :----------------------------------------------------------------------------------------------------------- | :---------------- |
| 🌐 **Web Tests**   | [rennangimenez.com/agibank-challenge/web/](https://rennangimenez.com/agibank-challenge/web/)                 | Allure interativo |
| 🐕 **API Tests**   | [rennangimenez.com/agibank-challenge/api/](https://rennangimenez.com/agibank-challenge/api/)                 | Allure interativo |
| ✈️ **Performance** | [rennangimenez.com/agibank-challenge/performance/](https://rennangimenez.com/agibank-challenge/performance/) | JMeter HTML       |
| 📈 **Dashboards**  | [rennangimenez.com/grafana/](https://rennangimenez.com/grafana/)                                             | Grafana live      |

### 🎨 Features dos Reports Allure

Os reports Allure foram enriquecidos com:

- 📸 **Screenshots automáticas** em todos os testes (sucesso e falha)
- 🔗 **URL final e título da página** capturados como evidência
- 🎬 **Playwright Traces** em falhas para debug visual passo-a-passo
- 📂 **Categorias customizadas** (Defeito de Produto, Defeito de Teste, Timeout, etc.)
- 🏷️ **Anotações ricas**: `@Epic`, `@Feature`, `@Story`, `@Owner`, `@Severity`, `@Link`
- 🌍 **Environment info**: Java version, browser, OS, branch, commit, runner
- 📈 **Trends históricos** com gráficos nativos de evolução
- 😊 **Nomes amigáveis com emojis** em todas as suites e testes (PT-BR)

### 🖥️ Reports Locais

```bash
# Allure — abre no browser automaticamente
./mvnw allure:serve -pl api-tests
./mvnw allure:serve -pl web-tests

# JMeter — HTML gerado em:
# performance-tests/target/jmeter/reports/
```

---

## 📈 Observabilidade

### Stack de Monitoramento

```
GitHub Actions → Testes → Allure/JMeter
                              │
            push-allure-metrics.sh ──→ InfluxDB ──→ Grafana
            push-pipeline-metrics.sh ↗
            JMeter Backend Listener ↗
```

### 📊 Dashboards Grafana

| Dashboard               | Métricas                                                          | Fonte                                                         |
| :---------------------- | :---------------------------------------------------------------- | :------------------------------------------------------------ |
| 📊 **Quality Overview** | Pass rate Web/API, trends, severidade, métricas cumulativas       | `test_results`, `test_severity`, `test_executions_cumulative` |
| ✈️ **Performance**      | Latência (avg/p90/p95/p99), throughput, error rate, por transação | `jmeter`                                                      |
| 🔄 **Pipeline Health**  | Success rate CI/CD, duração por job, feedback time                | `pipeline_runs`                                               |

### Infraestrutura

```
infra/
├── 🐳 docker-compose.yml                Grafana + InfluxDB + Prometheus
├── 📡 prometheus/prometheus.yml          Config de scrape
├── 📊 grafana/
│   ├── provisioning/                     Datasources e providers auto-config
│   └── dashboards/
│       ├── quality-overview.json         Resultados Web/API, pass rates, trends
│       ├── performance.json              JMeter latência, throughput, erros
│       └── pipeline-health.json          CI/CD success rate, duração, feedback
└── 📜 scripts/
    ├── push-allure-metrics.sh            Resultados + severidade + cumulativos → InfluxDB
    └── push-pipeline-metrics.sh          Duração + status de jobs → InfluxDB
```

---

## 🔄 CI/CD

### Workflows

| Workflow                 | Trigger          | Schedule         | O que faz                                                            |
| :----------------------- | :--------------- | :--------------- | :------------------------------------------------------------------- |
| 🔄 **`ci.yml`**          | Push `main`, PRs | 🕐 08h e 16h BRT | Deploy monitoring → API + Web tests → Deploy reports → Push métricas |
| ✈️ **`performance.yml`** | Manual           | 🕐 00h BRT       | JMeter test plan → HTML report → Push métricas pipeline              |

### Pipeline CI/CD (`ci.yml`)

```
┌────────────────────┐
│  Deploy Monitoring │  ← Sync Grafana + InfluxDB + Prometheus
└─────────┬──────────┘
          │
    ┌─────┴─────┐
    │           │
┌───▼───┐  ┌───▼───┐
│  API  │  │  Web  │   ← Testes em paralelo
│ Tests │  │ Tests │
└───┬───┘  └───┬───┘
    │          │
    ▼          ▼
  Allure     Allure      ← Gera reports com evidências
  Report     Report
    │          │
    ▼          ▼
  Deploy    Deploy        ← rsync para VPS
    │          │
    ▼          ▼
  Push      Push          ← Métricas para InfluxDB
  Metrics   Metrics
```

### Infraestrutura

| Recurso           | Detalhe                                                        |
| :---------------- | :------------------------------------------------------------- |
| 🖥️ **Runner**     | Self-hosted GitHub Actions na VPS (`agibank-vps`)              |
| 📁 **Reports**    | Deploy via `rsync` para `/srv/apps/agibank-challenge-reports/` |
| 📈 **Monitoring** | Grafana em `rennangimenez.com/grafana/`                        |
| 🌐 **Serving**    | Nginx em `rennangimenez.com/agibank-challenge/`                |

---

## 🧹 Qualidade de Código

| Ferramenta          | Função                 | Comando                    |
| :------------------ | :--------------------- | :------------------------- |
| ☕ **Spotless**     | Google Java Format     | `./mvnw spotless:apply`    |
| 💅 **Prettier**     | YAML, JSON, Markdown   | `npx prettier --write .`   |
| 🐶 **Husky**        | Pre-commit hooks       | Automático no `git commit` |
| 📐 **EditorConfig** | Indentação consistente | Automático nos editores    |

O pre-commit hook roda automaticamente:

1. **lint-staged** — Prettier nos arquivos staged
2. **Spotless check** — Verifica formatação Java
3. ❌ Bloqueia commit se houver violação

---

## 📝 Documentação

| Documento                       | Conteúdo                                         | Link                                                     |
| :------------------------------ | :----------------------------------------------- | :------------------------------------------------------- |
| 📋 **Plano de Testes**          | 42 cenários detalhados + 25 next steps propostos | [docs/test-plan.md](docs/test-plan.md)                   |
| 🧪 **Relatório de Testes**      | Resultados Web + API com análise detalhada       | [docs/test-report.md](docs/test-report.md)               |
| ✈️ **Relatório de Performance** | Load + spike test com métricas e análise         | [docs/performance-report.md](docs/performance-report.md) |

---

## 🗂️ Estrutura do Projeto

```
├── 🐕 api-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/br/com/agibank/qa/api/
│       │   ├── client/
│       │   │   └── DogApiClient.java              ← Camada HTTP
│       │   ├── fixtures/
│       │   │   ├── BreedData.java                 ← Dados: raças, URLs, contagens
│       │   │   └── SecurityPayloads.java          ← Payloads: SQL, XSS, etc.
│       │   └── tests/
│       │       ├── BreedListTest.java              ← 📋 Listagem de Raças
│       │       ├── BreedImagesTest.java            ← 🖼️ Imagens por Raça
│       │       ├── RandomImageTest.java            ← 🎲 Imagem Aleatória
│       │       ├── EdgeCaseTest.java               ← ⚠️ Casos Extremos
│       │       └── SecurityTest.java               ← 🛡️ Segurança
│       └── resources/
│           ├── schemas/                            ← JSON Schemas de contrato
│           └── categories.json                     ← Categorias Allure
│
├── 🌐 web-tests/
│   ├── pom.xml
│   └── src/test/java/br/com/agibank/qa/web/
│       ├── base/
│       │   └── BaseTest.java                       ← Lifecycle Playwright + tracing
│       ├── extensions/
│       │   └── ScreenshotOnResultExtension.java    ← 📸 Captura automática de evidências
│       ├── pages/
│       │   ├── BlogHomePage.java                   ← Page Object: home
│       │   └── SearchResultsPage.java              ← Page Object: resultados
│       ├── fixtures/
│       │   ├── SearchData.java                     ← Termos de busca + payloads
│       │   └── ExpectedResults.java                ← URLs, seletores esperados
│       └── tests/
│           ├── BlogSmokeTest.java                  ← 💨 Smoke Tests
│           ├── BlogSearchTest.java                 ← 🔍 Busca
│           └── BlogSecurityTest.java               ← 🛡️ Segurança
│
├── ✈️ performance-tests/
│   ├── pom.xml
│   └── src/test/jmeter/
│       ├── blazedemo-load-test.jmx                 ← ⚡ Carga sustentada
│       └── blazedemo-spike-test.jmx                ← 📈 Spike/burst
│
├── 📊 infra/
│   ├── docker-compose.yml                          ← Grafana + InfluxDB + Prometheus
│   ├── grafana/dashboards/                         ← 3 dashboards provisionados
│   └── scripts/
│       ├── push-allure-metrics.sh                  ← Resultados + severidade → InfluxDB
│       └── push-pipeline-metrics.sh                ← CI/CD duração + status → InfluxDB
│
├── 🔄 .github/workflows/
│   ├── ci.yml                                      ← CI: API + Web + métricas
│   └── performance.yml                             ← Performance: manual + cron
│
├── 📝 docs/
│   ├── test-plan.md                                ← 📋 Plano completo (42 cenários)
│   ├── test-report.md                              ← 🧪 Resultados Web + API
│   └── performance-report.md                       ← ✈️ Resultados de performance
│
├── 📦 pom.xml                                      ← Parent POM
├── 🔧 mvnw / mvnw.cmd                              ← Maven Wrapper
├── 📗 package.json                                  ← Husky + Prettier
└── 📖 README.md                                     ← Você está aqui! 👋
```

---

## 👨‍💻 Autor

<table>
  <tr>
    <td align="center">
      <strong>Rennan Gimenez</strong><br />
      <a href="https://github.com/rennangimenez">GitHub</a> · <a href="https://rennangimenez.com">Portfolio</a>
    </td>
  </tr>
</table>

---

<p align="center">
  Feito com ☕ e muita dedicação para o desafio AgiBank QA.
</p>
