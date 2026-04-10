# 📋 Plano de Testes — AgiBank QA Automation Challenge

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 09/04/2026
**Versao:** 2.0

---

## 📌 1. Objetivo

Este plano de testes documenta a estrategia completa de automacao de testes para o desafio tecnico de **QA Senior** do AgiBank, cobrindo tres pilares: **Web**, **API** e **Performance**.

O objetivo e validar a **qualidade**, **confiabilidade** e **seguranca** das aplicacoes alvo, demonstrando senioridade tecnica em:

- 🏗️ Arquitetura de testes escalavel
- 🔐 Testes de seguranca ofensivos
- 📊 Observabilidade e metricas em tempo real
- 🔄 CI/CD automatizado com execucao programada

---

## 🎯 2. Escopo

### 2.1 Aplicacoes Alvo

| #   | Aplicacao      | Tipo     | URL                      | Escopo                         |
| --- | -------------- | -------- | ------------------------ | ------------------------------ |
| 1   | 🌐 Blog do Agi | Web      | https://blogdoagi.com.br | Busca, navegacao, seguranca    |
| 2   | 🔗 Dog API     | REST API | https://dog.ceo/api      | Endpoints, contrato, seguranca |
| 3   | ✈️ BlazeDemo   | Web App  | https://blazedemo.com    | Fluxo de compra sob carga      |

### 2.2 Tipos de Teste Cobertos

| Tipo            | Icone | Descricao                                                                 |
| --------------- | ----- | ------------------------------------------------------------------------- |
| Funcional (Web) | 🌐    | Busca, componentes UI, smoke tests                                        |
| Funcional (API) | 🔗    | Endpoints, contrato JSON Schema, edge cases                               |
| Seguranca (Web) | 🔒    | SQL Injection, XSS, HTML Injection                                        |
| Seguranca (API) | 🛡️    | SQL Injection, Path Traversal, Information Disclosure, Headers maliciosos |
| Performance     | ⚡    | Carga sustentada (load) e pico (spike)                                    |

### 2.3 Fora de Escopo

- ❌ Testes de acessibilidade (WCAG)
- ❌ Testes cross-browser (foco em Chromium)
- ❌ Testes de integracao entre as aplicacoes alvo

---

## 🏗️ 3. Stack Tecnologica

| Camada           | Tecnologia         | Versao | Proposito                      |
| ---------------- | ------------------ | ------ | ------------------------------ |
| 💻 Linguagem     | Java               | 17     | Linguagem principal            |
| 📦 Build         | Maven              | 3.9.6  | Multi-module com Maven Wrapper |
| 🧪 Test Runner   | JUnit 5            | 5.10.2 | Framework unificado            |
| 🌐 Web           | Playwright Java    | 1.44.0 | Automacao com auto-waits       |
| 🔗 API           | RestAssured        | 5.4.0  | HTTP client fluente            |
| ⚡ Performance   | JMeter             | 5.6.3  | Carga e pico                   |
| 📊 Reporting     | Allure             | 2.25.0 | Reports interativos            |
| 👁️ Observability | Grafana + InfluxDB | Latest | Dashboards tempo real          |
| 🔄 CI/CD         | GitHub Actions     | N/A    | Self-hosted runner VPS         |
| ✨ Code Quality  | Spotless + Husky   | N/A    | Formatacao e pre-commit        |

---

## 🧪 4. Casos de Teste

### 4.1 🌐 Web Tests — Blog do Agi (14 testes)

#### 4.1.1 Search (BlogSearchTest) — 3 testes

| ID      | Cenario                                                                         | Prioridade  | Tipo      | Padrao AAA                                                                         |
| ------- | ------------------------------------------------------------------------------- | ----------- | --------- | ---------------------------------------------------------------------------------- |
| WEB-001 | ✅ Busca com termo valido ("emprestimo") retorna resultados com titulos e links | 🔴 Critical | Funcional | Arrange: `SearchData.VALID_TERM` / Act: `searchFor()` / Assert: 5 validacoes       |
| WEB-002 | ✅ Busca com termo inexistente mostra mensagem "nada encontrado"                | 🟡 Normal   | Funcional | Arrange: `SearchData.NONEXISTENT_TERM` / Act: `searchFor()` / Assert: 3 validacoes |
| WEB-003 | ✅ Componentes de busca (icone + form) presentes no DOM                         | 🟡 Normal   | Funcional | Act: `navigate()` / Assert: 3 validacoes de UI                                     |

#### 4.1.2 Smoke Tests (BlogSmokeTest) — 6 testes

| ID      | Cenario                                                                   | Prioridade  | Tipo  |
| ------- | ------------------------------------------------------------------------- | ----------- | ----- |
| WEB-004 | ✅ Home page carrega com sucesso (HTTP 200, URL correta, titulo presente) | 🔴 Blocker  | Smoke |
| WEB-005 | ✅ Navegacao principal `<nav>` esta visivel                               | 🔴 Critical | Smoke |
| WEB-006 | ✅ Logo presente no header (img/svg)                                      | 🟡 Normal   | Smoke |
| WEB-007 | ✅ Footer esta visivel na pagina                                          | 🟡 Normal   | Smoke |
| WEB-008 | ✅ Pagina contem ao menos 1 `<article>`                                   | 🔴 Critical | Smoke |
| WEB-009 | ✅ Sem erros criticos de JavaScript no console (exclui 404 de recursos)   | 🟡 Normal   | Smoke |

#### 4.1.3 Security Tests (BlogSecurityTest) — 5 testes

| ID      | Cenario                                                    | Prioridade  | Tipo      | Payload                         |
| ------- | ---------------------------------------------------------- | ----------- | --------- | ------------------------------- |
| WEB-010 | ✅ SQL Injection no campo de busca nao expoe dados         | 🔴 Critical | Seguranca | `'; DROP TABLE users;--`        |
| WEB-011 | ✅ XSS `<script>` nao executa (nenhum dialog JS disparado) | 🔴 Critical | Seguranca | `<script>alert('xss')</script>` |
| WEB-012 | ⚠️ **[FINDING]** HTML Injection renderiza como DOM real    | 🔴 Critical | Seguranca | `<h1>Injected</h1>`             |
| WEB-013 | ✅ Input de 5.000 caracteres nao causa crash               | 🟡 Normal   | Seguranca | `"a".repeat(5000)`              |
| WEB-014 | ✅ Caracteres especiais `<>&"'%\` sao tratados             | 🟡 Normal   | Seguranca | `<>&"'%\`                       |

---

### 4.2 🔗 API Tests — Dog API (26 testes)

#### 4.2.1 Breed List (BreedListTest) — 4 testes

| ID      | Cenario                                                       | Prioridade  | Tipo      |
| ------- | ------------------------------------------------------------- | ----------- | --------- |
| API-001 | ✅ GET `/breeds/list/all` retorna 200 + `"status": "success"` | 🔴 Blocker  | Funcional |
| API-002 | ✅ Lista contem racas conhecidas (bulldog, labrador, hound)   | 🔴 Critical | Funcional |
| API-003 | ✅ Sub-racas sao arrays (mesmo quando vazias)                 | 🟡 Normal   | Funcional |
| API-004 | ✅ Response conforma com `breed-list-schema.json`             | 🔴 Critical | Contrato  |

#### 4.2.2 Breed Images (BreedImagesTest) — 4 testes

| ID      | Cenario                                                    | Prioridade  | Tipo      |
| ------- | ---------------------------------------------------------- | ----------- | --------- |
| API-005 | ✅ GET `/breed/hound/images` retorna 200 com lista de URLs | 🔴 Critical | Funcional |
| API-006 | ✅ Todas URLs iniciam com `https://images.dog.ceo/breeds/` | 🟡 Normal   | Funcional |
| API-007 | ✅ URLs contem nome da raca no path                        | 🟡 Normal   | Funcional |
| API-008 | ✅ Raca invalida retorna 404 + `"status": "error"`         | 🔴 Critical | Funcional |

#### 4.2.3 Random Image (RandomImageTest) — 3 testes

| ID      | Cenario                                                      | Prioridade  | Tipo      |
| ------- | ------------------------------------------------------------ | ----------- | --------- |
| API-009 | ✅ GET `/breeds/image/random` retorna 200 com URL valida     | 🔴 Blocker  | Funcional |
| API-010 | ✅ URL tem extensao de imagem valida (jpg/jpeg/png/gif/webp) | 🟡 Normal   | Funcional |
| API-011 | ✅ Response conforma com `image-response-schema.json`        | 🔴 Critical | Contrato  |

#### 4.2.4 Edge Cases (EdgeCaseTest) — 9 testes

| ID      | Cenario                                                  | Prioridade  | Tipo      |
| ------- | -------------------------------------------------------- | ----------- | --------- |
| API-012 | ✅ Raca com `@#$` retorna 404                            | 🟡 Normal   | Edge Case |
| API-013 | ✅ Nome numerico (`12345`) retorna 404                   | 🟡 Normal   | Edge Case |
| API-014 | ✅ Nome de 200 caracteres retorna 404 ou 414             | 🟡 Normal   | Edge Case |
| API-015 | ✅ Unicode (`cafe☕🐕`) nao causa 5xx                    | 🟡 Normal   | Edge Case |
| API-016 | ✅ `/breeds/image/random/5` retorna exatamente 5 imagens | 🔴 Critical | Funcional |
| API-017 | ✅ Count zero retorna 200 ou 400                         | 🟡 Normal   | Edge Case |
| API-018 | ✅ Count negativo nao causa 5xx                          | 🟡 Normal   | Edge Case |
| API-019 | ✅ Sub-raca valida (`hound/afghan`) retorna imagens      | 🔴 Critical | Funcional |
| API-020 | ✅ Sub-raca invalida retorna 404                         | 🟡 Normal   | Funcional |

#### 4.2.5 Security Tests (SecurityTest) — 6 testes

| ID      | Cenario                                                      | Prioridade  | Tipo      | Payload                     |
| ------- | ------------------------------------------------------------ | ----------- | --------- | --------------------------- |
| API-021 | ✅ SQL Injection `' OR '1'='1` nao expoe dados SQL           | 🔴 Critical | Seguranca | `' OR '1'='1`               |
| API-022 | ✅ Path Traversal `../../../etc/passwd` nao expoe filesystem | 🔴 Critical | Seguranca | `../../../etc/passwd`       |
| API-023 | ⚠️ **[FINDING]** `X-Powered-By: PHP/8.3.29` exposto          | 🟡 Normal   | Seguranca | N/A (header check)          |
| API-024 | ✅ `<script>alert(1)</script>` nao e refletido na response   | 🔴 Critical | Seguranca | `<script>alert(1)</script>` |
| API-025 | ✅ Content-Type malicioso (XXE entity) nao causa 5xx         | 🟡 Normal   | Seguranca | XXE entity payload          |
| API-026 | ✅ Header de 8.000 chars nao causa crash                     | 🟡 Normal   | Seguranca | `"A".repeat(8000)`          |

---

### 4.3 ⚡ Performance Tests — BlazeDemo (2 cenarios)

#### 4.3.1 Load Test (blazedemo-load-test.jmx)

| ID       | Cenario                             | Config                              | Criterio            |
| -------- | ----------------------------------- | ----------------------------------- | ------------------- |
| PERF-001 | Carga sustentada no fluxo de compra | 150 threads, ramp 60s, duracao 240s | 250 req/s, p90 < 2s |

**Fluxo testado:**

1. 🏠 `GET /` — Home Page
2. 🔍 `POST /reserve.php` — Buscar voos
3. ✈️ `POST /purchase.php` — Selecionar voo
4. ✅ `POST /confirmation.php` — Confirmar compra

#### 4.3.2 Spike Test (blazedemo-spike-test.jmx)

| ID       | Cenario                      | Config                                                | Criterio              |
| -------- | ---------------------------- | ----------------------------------------------------- | --------------------- |
| PERF-002 | Burst de trafego com 3 fases | Fase 1: 30t/60s → Fase 2: 200t/120s → Fase 3: 30t/60s | Recuperacao pos-spike |

---

## 🔐 5. Findings de Seguranca

### 5.1 ⚠️ [WEB-012] HTML Injection — Blog do Agi

| Campo             | Detalhe                                                      |
| ----------------- | ------------------------------------------------------------ |
| **Severidade**    | 🔴 Critical                                                  |
| **Tipo**          | HTML Injection (Reflected)                                   |
| **Localizacao**   | Campo de busca → pagina de resultados                        |
| **Payload**       | `<h1>Injected</h1>`                                          |
| **Comportamento** | Tag `<h1>` renderizada como elemento DOM real                |
| **Impacto**       | Injecao de conteudo visual, potencial phishing               |
| **Recomendacao**  | Sanitizar output do termo de busca no template de resultados |

### 5.2 ⚠️ [API-023] Information Disclosure — Dog API

| Campo            | Detalhe                                                |
| ---------------- | ------------------------------------------------------ |
| **Severidade**   | 🟡 Normal                                              |
| **Tipo**         | Information Disclosure                                 |
| **Header**       | `X-Powered-By: PHP/8.3.29`                             |
| **Impacto**      | Expoe tecnologia e versao do backend                   |
| **Recomendacao** | Remover ou suprimir o header `X-Powered-By` no php.ini |

---

## 📊 6. Padrao AAA (Arrange-Act-Assert)

Todos os testes seguem o padrao **AAA hibrido** com camadas de suporte:

```
Arrange → Dados vem dos fixtures/ (SearchData, BreedData, SecurityPayloads)
Act     → Acoes executadas via pages/ (Web) ou client/ (API)
Assert  → Validacoes explicitas com assertAll() para multiplas verificacoes
```

**Camadas do projeto:**

| Camada               | Web                                 | API                             | Responsabilidade             |
| -------------------- | ----------------------------------- | ------------------------------- | ---------------------------- |
| `fixtures/`          | `SearchData`, `ExpectedResults`     | `BreedData`, `SecurityPayloads` | Dados de teste centralizados |
| `pages/` / `client/` | `BlogHomePage`, `SearchResultsPage` | `DogApiClient`                  | Acoes (Act)                  |
| `tests/`             | `BlogSearchTest`, etc.              | `BreedListTest`, etc.           | Orquestracao AAA             |
| `base/`              | `BaseTest`                          | —                               | Lifecycle do Playwright      |

---

## 🔄 7. Estrategia de Execucao

### 7.1 Execucao Local

```bash
# API Tests
./mvnw clean test -pl api-tests

# Web Tests
./mvnw clean test -pl web-tests

# Performance — Load Test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-load-test

# Performance — Spike Test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-spike-test
```

### 7.2 Execucao CI/CD (GitHub Actions)

| Workflow          | Trigger        | Schedule (BRT) | Descricao                                          |
| ----------------- | -------------- | -------------- | -------------------------------------------------- |
| `ci.yml`          | Push/PR + Cron | 🕐 08h e 16h   | API + Web tests, Allure reports, metricas InfluxDB |
| `performance.yml` | Manual + Cron  | 🕐 00h         | JMeter test plan, HTML report, metricas pipeline   |

### 7.3 Comparacao Local vs CI/CD

| Aspecto        | Local                         | CI/CD (VPS)                     |
| -------------- | ----------------------------- | ------------------------------- |
| **Ambiente**   | Windows 11 / rede residencial | Ubuntu / VPS dedicada           |
| **Latencia**   | Variavel (ISP)                | Estavel (datacenter)            |
| **Execucao**   | Manual (`./mvnw`)             | Automatica (push/cron)          |
| **Reports**    | Terminal / Allure local       | Allure online + Grafana         |
| **Metricas**   | Nenhuma                       | InfluxDB + Grafana dashboards   |
| **Reproducao** | Depende do setup local        | 100% reproduzivel (runner fixo) |

---

## 👁️ 8. Observabilidade

### 8.1 Stack

```
GitHub Actions → Allure/JMeter → push-allure-metrics.sh → InfluxDB → Grafana
                                  push-pipeline-metrics.sh ↗
```

### 8.2 Dashboards Grafana

| Dashboard               | Fonte de Dados                   | Metricas                                           |
| ----------------------- | -------------------------------- | -------------------------------------------------- |
| 📊 Quality Overview     | `test_results`, `test_stability` | Pass rate, trends, flaky tests, por suite          |
| ⚡ Performance (JMeter) | `jmeter`                         | Latencia (avg/p90/p95/p99), throughput, error rate |
| 🔄 Pipeline Health      | `pipeline_runs`                  | Success rate, duracao por job, feedback time       |

**URL:** https://rennangimenez.com/grafana/

---

## 📈 9. Reports e Evidencias

| Report                  | Formato         | URL                                                      |
| ----------------------- | --------------- | -------------------------------------------------------- |
| 🌐 Allure — Web Tests   | HTML interativo | https://rennangimenez.com/agibank-challenge/web/         |
| 🔗 Allure — API Tests   | HTML interativo | https://rennangimenez.com/agibank-challenge/api/         |
| ⚡ JMeter — Performance | HTML            | https://rennangimenez.com/agibank-challenge/performance/ |
| 👁️ Grafana — Dashboards | Web App         | https://rennangimenez.com/grafana/                       |

---

## ⚠️ 10. Riscos e Mitigacoes

| Risco                       | Prob.    | Impacto  | Mitigacao                                                 |
| --------------------------- | -------- | -------- | --------------------------------------------------------- |
| Blog muda dominio/estrutura | 🟡 Media | 🔴 Alto  | URL navigation direta, locators semanticos                |
| Dog API indisponivel        | 🟢 Baixa | 🔴 Alto  | Testes falham gracefully, CI tem timeout                  |
| BlazeDemo fora do ar        | 🟢 Baixa | 🔴 Alto  | Performance via workflow_dispatch + cron                  |
| VPS runner offline          | 🟢 Baixa | 🔴 Alto  | Monitoramento Grafana + alertas                           |
| Flakiness em testes web     | 🟡 Media | 🟡 Medio | Auto-waits Playwright, sem clicks em elementos invisiveis |

---

## 🚀 11. Next Steps — 25 Novos Cenarios Propostos

### 11.1 🌐 Web (10 cenarios)

| ID        | Cenario Proposto                                   | Tipo           | Justificativa       |
| --------- | -------------------------------------------------- | -------------- | ------------------- |
| WEB-NS-01 | Validar breadcrumbs na pagina de artigo            | Funcional      | Navegacao e SEO     |
| WEB-NS-02 | Verificar tags/categorias nos posts                | Funcional      | Taxonomia do blog   |
| WEB-NS-03 | Testar paginacao da home page                      | Funcional      | UX em listas longas |
| WEB-NS-04 | Validar compartilhamento social (botoes presentes) | Funcional      | Engajamento         |
| WEB-NS-05 | Testar responsividade mobile (viewport 375px)      | UI/UX          | Mobile first        |
| WEB-NS-06 | Verificar metatags Open Graph para SEO             | SEO            | Social sharing      |
| WEB-NS-07 | Testar navegacao por teclado (Tab + Enter)         | Acessibilidade | WCAG basico         |
| WEB-NS-08 | Injecao de iframe via busca                        | Seguranca      | Iframe injection    |
| WEB-NS-09 | Testar busca com emojis e unicode                  | Edge Case      | Internacionalizacao |
| WEB-NS-10 | Performance audit com Lighthouse (via Playwright)  | Performance    | Core Web Vitals     |

### 11.2 🔗 API (10 cenarios)

| ID        | Cenario Proposto                                        | Tipo        | Justificativa      |
| --------- | ------------------------------------------------------- | ----------- | ------------------ |
| API-NS-01 | Validar CORS headers na response                        | Seguranca   | Cross-origin       |
| API-NS-02 | Testar rate limiting (burst de requests)                | Performance | Resiliencia        |
| API-NS-03 | Validar Content-Type correto (application/json)         | Contrato    | Consistencia       |
| API-NS-04 | Testar cache headers (ETag, Cache-Control)              | Performance | Eficiencia         |
| API-NS-05 | Validar que metodos nao suportados retornam 405         | Contrato    | RESTful compliance |
| API-NS-06 | Testar concurrent requests ao mesmo endpoint            | Performance | Thread safety      |
| API-NS-07 | Validar idempotencia de GETs consecutivos               | Funcional   | Consistencia       |
| API-NS-08 | Testar encoding URL com caracteres reservados           | Edge Case   | RFC compliance     |
| API-NS-09 | Validar response time SLA (< 500ms para lista de racas) | Performance | SLA                |
| API-NS-10 | Command injection via breed parameter                   | Seguranca   | OS injection       |

### 11.3 ⚡ Performance (5 cenarios)

| ID         | Cenario Proposto                                           | Tipo      | Justificativa       |
| ---------- | ---------------------------------------------------------- | --------- | ------------------- |
| PERF-NS-01 | Stress test — aumentar threads ate quebrar                 | Stress    | Limite do sistema   |
| PERF-NS-02 | Endurance test — carga baixa por 1 hora                    | Endurance | Memory leaks        |
| PERF-NS-03 | Teste de carga somente no endpoint de confirmacao          | Isolado   | Bottleneck analysis |
| PERF-NS-04 | Teste com think time zero (worst case)                     | Stress    | Throughput maximo   |
| PERF-NS-05 | Comparativo de performance entre periodos (manha vs noite) | Baseline  | Variacao temporal   |

---

## 📌 12. Conclusao

Este plano cobre **42 cenarios de teste implementados** (14 web + 26 api + 2 performance) distribuidos em 3 pilares com foco adicional em seguranca e observabilidade. A arquitetura segue o padrao AAA hibrido com camadas de fixtures, pages/client e tests, garantindo separacao de responsabilidades e facilidade de manutencao.

A execucao e automatizada via CI/CD com schedule programado e metricas em tempo real via Grafana, proporcionando visibilidade completa do estado de qualidade do projeto.

**25 novos cenarios propostos** como proximos passos demonstram a visao de evolucao continua da suite de testes.
