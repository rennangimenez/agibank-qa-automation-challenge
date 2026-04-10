# Plano de Testes - AgiBank QA Automation Challenge

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 09/04/2026
**Versao:** 1.0

---

## 1. Objetivo

Este documento descreve o plano de testes completo para o desafio tecnico de QA Senior do AgiBank, cobrindo tres pilares de automacao: **Web**, **API** e **Performance**. O objetivo e validar a qualidade, confiabilidade e seguranca das aplicacoes alvo, demonstrando senioridade tecnica em arquitetura de testes, boas praticas e observabilidade.

---

## 2. Escopo

### 2.1 Aplicacoes Alvo

| Aplicacao   | Tipo     | URL                      | Escopo de Teste                     |
| ----------- | -------- | ------------------------ | ----------------------------------- |
| Blog do Agi | Web      | https://blogdoagi.com.br | Busca, navegacao, seguranca         |
| Dog API     | REST API | https://dog.ceo/api      | Endpoints CRUD, contrato, seguranca |
| BlazeDemo   | Web App  | https://blazedemo.com    | Fluxo de compra sob carga           |

### 2.2 Tipos de Teste

- **Funcionais (Web):** Validacao de busca, componentes UI, smoke tests
- **Funcionais (API):** Validacao de endpoints, contrato JSON Schema, edge cases
- **Seguranca (Web):** SQL Injection, XSS, HTML Injection
- **Seguranca (API):** SQL Injection, Path Traversal, Information Disclosure, Headers maliciosos
- **Performance:** Testes de carga (load) e pico (spike)

### 2.3 Fora de Escopo

- Testes de acessibilidade (WCAG)
- Testes de compatibilidade cross-browser
- Testes de integracao entre as aplicacoes alvo

---

## 3. Arquitetura de Testes

### 3.1 Stack Tecnologica

| Camada        | Tecnologia                      | Versao | Proposito                            |
| ------------- | ------------------------------- | ------ | ------------------------------------ |
| Linguagem     | Java                            | 17     | Linguagem principal                  |
| Build         | Maven                           | 3.9.6  | Build multi-module com Maven Wrapper |
| Test Runner   | JUnit 5                         | 5.10.2 | Framework de testes unificado        |
| Web           | Playwright Java                 | 1.44.0 | Automacao de browser com auto-waits  |
| API           | RestAssured                     | 5.4.0  | HTTP client com assertions fluentes  |
| Performance   | JMeter                          | 5.6.3  | Testes de carga e pico               |
| Reporting     | Allure                          | 2.25.0 | Reports interativos (Web + API)      |
| Observability | Grafana + InfluxDB + Prometheus | Latest | Dashboards em tempo real             |
| CI/CD         | GitHub Actions                  | N/A    | Self-hosted runner na VPS            |
| Code Quality  | Spotless + Husky + Prettier     | N/A    | Formatacao e pre-commit hooks        |

### 3.2 Estrutura do Projeto

```
agibank-qa-automation-challenge/
├── web-tests/                    Playwright Java (Blog do Agi)
│   ├── pages/                    Page Objects
│   ├── tests/                    Test Classes
│   └── base/                     BaseTest (lifecycle)
├── api-tests/                    RestAssured (Dog API)
│   ├── client/                   HTTP client layer (DogApiClient)
│   ├── tests/                    Test Classes
│   └── resources/schemas/        JSON Schema files
├── performance-tests/            JMeter (BlazeDemo)
│   └── src/test/jmeter/          Test Plans (.jmx)
├── infra/                        Monitoring Stack
│   ├── docker-compose.yml        Grafana + InfluxDB + Prometheus
│   ├── grafana/dashboards/       Dashboard JSONs
│   └── scripts/                  Automation scripts
├── .github/workflows/            CI/CD Pipelines
└── docs/                         Documentacao
```

### 3.3 Padroes de Design

- **Page Object Model (Web):** Separacao entre acoes de pagina e assertions de teste
- **Client Layer (API):** `DogApiClient` encapsula toda logica HTTP, isolando testes das chamadas
- **BaseTest Pattern:** Lifecycle do Playwright (browser/context/page) gerenciado pelo `BaseTest`
- **JSON Schema Validation:** Validacao de contrato estrutural alem de status codes

---

## 4. Casos de Teste

### 4.1 Web Tests - Blog do Agi

#### 4.1.1 Search (BlogSearchTest)

| ID      | Cenario                                                          | Prioridade | Tipo      |
| ------- | ---------------------------------------------------------------- | ---------- | --------- |
| WEB-001 | Busca com termo valido retorna resultados                        | Critical   | Funcional |
| WEB-002 | Busca com termo inexistente mostra mensagem de "nada encontrado" | Normal     | Funcional |
| WEB-003 | Componentes de busca estao presentes no DOM                      | Normal     | Funcional |

#### 4.1.2 Smoke Tests (BlogSmokeTest)

| ID      | Cenario                                          | Prioridade | Tipo  |
| ------- | ------------------------------------------------ | ---------- | ----- |
| WEB-004 | Home page carrega com sucesso (HTTP 200)         | Blocker    | Smoke |
| WEB-005 | Navegacao principal e visivel                    | Critical   | Smoke |
| WEB-006 | Logo esta presente na pagina                     | Normal     | Smoke |
| WEB-007 | Footer esta presente na pagina                   | Normal     | Smoke |
| WEB-008 | Pagina contem artigos/posts                      | Critical   | Smoke |
| WEB-009 | Sem erros criticos de JavaScript no carregamento | Normal     | Smoke |

#### 4.1.3 Security Tests (BlogSecurityTest)

| ID      | Cenario                                                   | Prioridade | Tipo      |
| ------- | --------------------------------------------------------- | ---------- | --------- |
| WEB-010 | SQL Injection no campo de busca e tratado com seguranca   | Critical   | Seguranca |
| WEB-011 | XSS attempt no campo de busca nao executa scripts         | Critical   | Seguranca |
| WEB-012 | [FINDING] HTML Injection renderiza como elemento DOM real | Critical   | Seguranca |
| WEB-013 | Input extremamente longo nao causa crash                  | Normal     | Seguranca |
| WEB-014 | Caracteres especiais nao causam erros                     | Normal     | Seguranca |

### 4.2 API Tests - Dog API

#### 4.2.1 Breed List (BreedListTest)

| ID      | Cenario                                                  | Prioridade | Tipo      |
| ------- | -------------------------------------------------------- | ---------- | --------- |
| API-001 | Listar todas as racas retorna 200 com status "success"   | Blocker    | Funcional |
| API-002 | Lista contem racas conhecidas (bulldog, labrador, hound) | Critical   | Funcional |
| API-003 | Sub-racas sao retornadas como arrays                     | Normal     | Funcional |
| API-004 | Resposta conforma com JSON Schema                        | Critical   | Contrato  |

#### 4.2.2 Breed Images (BreedImagesTest)

| ID      | Cenario                                     | Prioridade | Tipo      |
| ------- | ------------------------------------------- | ---------- | --------- |
| API-005 | Imagens de raca valida retorna 200 com URLs | Critical   | Funcional |
| API-006 | URLs de imagens apontam para images.dog.ceo | Normal     | Funcional |
| API-007 | URLs de imagens contem o nome da raca       | Normal     | Funcional |
| API-008 | Raca invalida retorna 404                   | Critical   | Funcional |

#### 4.2.3 Random Image (RandomImageTest)

| ID      | Cenario                                     | Prioridade | Tipo      |
| ------- | ------------------------------------------- | ---------- | --------- |
| API-009 | Imagem aleatoria retorna 200 com URL valida | Blocker    | Funcional |
| API-010 | URL tem extensao de imagem valida           | Normal     | Funcional |
| API-011 | Resposta conforma com JSON Schema           | Critical   | Contrato  |

#### 4.2.4 Edge Cases (EdgeCaseTest)

| ID      | Cenario                                               | Prioridade | Tipo      |
| ------- | ----------------------------------------------------- | ---------- | --------- |
| API-012 | Raca com caracteres especiais retorna 404             | Normal     | Edge Case |
| API-013 | Nome numerico de raca retorna 404                     | Normal     | Edge Case |
| API-014 | Nome de raca muito longo retorna 404                  | Normal     | Edge Case |
| API-015 | Nome de raca com unicode e tratado sem crash          | Normal     | Edge Case |
| API-016 | Multiplas imagens aleatorias retorna contagem correta | Critical   | Funcional |
| API-017 | Imagens aleatorias com count zero e tratado           | Normal     | Edge Case |
| API-018 | Imagens aleatorias com count negativo e tratado       | Normal     | Edge Case |
| API-019 | Sub-raca valida retorna imagens                       | Critical   | Funcional |
| API-020 | Sub-raca invalida retorna 404                         | Normal     | Funcional |

#### 4.2.5 Security Tests (SecurityTest)

| ID      | Cenario                                              | Prioridade | Tipo      |
| ------- | ---------------------------------------------------- | ---------- | --------- |
| API-021 | SQL Injection no parametro de raca e tratado         | Critical   | Seguranca |
| API-022 | Path Traversal no parametro de raca e tratado        | Critical   | Seguranca |
| API-023 | [FINDING] API expoe X-Powered-By header (PHP/8.3.29) | Normal     | Seguranca |
| API-024 | XSS payload no parametro nao e refletido             | Critical   | Seguranca |
| API-025 | Content-Type malicioso e tratado                     | Normal     | Seguranca |
| API-026 | Header oversized e tratado gracefully                | Normal     | Seguranca |

### 4.3 Performance Tests - BlazeDemo

#### 4.3.1 Load Test (blazedemo-load-test.jmx)

| ID       | Cenario                             | Config                              | Criterio            |
| -------- | ----------------------------------- | ----------------------------------- | ------------------- |
| PERF-001 | Carga sustentada no fluxo de compra | 150 threads, ramp 60s, duracao 240s | 250 req/s, p90 < 2s |

**Fluxo testado:**

1. GET / (Home Page)
2. POST /reserve.php (Buscar voos)
3. POST /purchase.php (Selecionar voo)
4. POST /confirmation.php (Confirmar compra)

#### 4.3.2 Spike Test (blazedemo-spike-test.jmx)

| ID       | Cenario                      | Config                                                                   | Criterio              |
| -------- | ---------------------------- | ------------------------------------------------------------------------ | --------------------- |
| PERF-002 | Burst de trafego com 3 fases | Fase 1: 30 threads/60s, Fase 2: 200 threads/120s, Fase 3: 30 threads/60s | Recuperacao pos-spike |

---

## 5. Criterios de Validacao

### 5.1 Web Tests

- Todos os testes devem passar em modo headless (Chromium)
- Page Objects devem usar auto-waits do Playwright
- Testes de seguranca documentam findings reais como assertions positivas

### 5.2 API Tests

- Status codes validados para cenarios positivos E negativos
- JSON Schema validation para endpoints criticos
- Findings de seguranca documentados com evidencia

### 5.3 Performance Tests

- **Throughput target:** 250 requests/segundo
- **Response time target:** p90 < 2 segundos
- **Error rate:** < 1%
- Metricas em tempo real via InfluxDB Backend Listener + Grafana

---

## 6. Estrategia de Execucao

### 6.1 Execucao Local

```bash
# API Tests
./mvnw clean test -pl api-tests

# Web Tests
./mvnw clean test -pl web-tests

# Performance - Load Test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-load-test

# Performance - Spike Test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-spike-test
```

### 6.2 Execucao CI/CD

| Workflow        | Trigger                    | Descricao                                                              |
| --------------- | -------------------------- | ---------------------------------------------------------------------- |
| ci.yml          | Push/PR para main          | Roda API + Web tests, gera Allure reports, push metricas para InfluxDB |
| performance.yml | Manual (workflow_dispatch) | Roda JMeter test plan selecionado, deploya HTML report                 |

### 6.3 Observabilidade

A stack de observabilidade usa 3 dashboards focados, provisionados como codigo:

| Dashboard                  | Fonte de Dados           | Proposito                                                         |
| -------------------------- | ------------------------ | ----------------------------------------------------------------- |
| Quality Overview           | InfluxDB (test_results)  | Resultados web/api separados, pass rate, trends, flaky tests      |
| Performance (JMeter)       | InfluxDB (jmeter)        | Latencia (avg/p90/p95/p99), throughput, error rate, por transacao |
| Pipeline & Delivery Health | InfluxDB (pipeline_runs) | Success rate, duracao por job, feedback time, falhas              |

- **InfluxDB:** Time-series database para metricas de performance (JMeter), resultados de teste (Allure) e pipeline CI/CD
- **Prometheus:** Coleta de metricas de infraestrutura
- **Grafana:** Visualizacao unificada com dashboards provisionados automaticamente via CI/CD

---

## 7. Reports e Evidencias

| Report               | Formato         | URL                                                      |
| -------------------- | --------------- | -------------------------------------------------------- |
| Allure - Web Tests   | HTML interativo | https://rennangimenez.com/agibank-challenge/web/         |
| Allure - API Tests   | HTML interativo | https://rennangimenez.com/agibank-challenge/api/         |
| JMeter - Performance | HTML            | https://rennangimenez.com/agibank-challenge/performance/ |
| Grafana - Dashboards | Web App         | https://rennangimenez.com/grafana/                       |

---

## 8. Findings de Seguranca

### 8.1 [WEB-012] HTML Injection no Blog do Agi

- **Severidade:** Critical
- **Descricao:** Tags HTML submetidas via campo de busca sao renderizadas como elementos DOM reais na pagina de resultados
- **Evidencia:** `<h1>Injected</h1>` renderiza como elemento `<h1>` real
- **Impacto:** Permite injecao de conteudo visual na pagina, potencial para phishing
- **Recomendacao:** Sanitizar output do termo de busca no template de resultados

### 8.2 [API-023] Information Disclosure via X-Powered-By Header

- **Severidade:** Normal
- **Descricao:** A Dog API retorna `X-Powered-By: PHP/8.3.29` nos headers de resposta
- **Impacto:** Expoe tecnologia e versao do backend, facilitando ataques direcionados
- **Recomendacao:** Remover ou suprimir o header `X-Powered-By` na configuracao do servidor

---

## 9. Riscos e Mitigacoes

| Risco                              | Probabilidade | Impacto | Mitigacao                                                         |
| ---------------------------------- | ------------- | ------- | ----------------------------------------------------------------- |
| Blog do Agi muda dominio/estrutura | Media         | Alto    | Testes usam URL navigation direta, locators baseados em semantica |
| Dog API indisponivel               | Baixa         | Alto    | Testes falham gracefully, CI tem timeout                          |
| BlazeDemo fora do ar               | Baixa         | Alto    | Performance tests sao manuais (workflow_dispatch)                 |
| VPS runner offline                 | Baixa         | Alto    | Monitoramento via Grafana + Node Exporter                         |
| Flakiness em testes web            | Media         | Medio   | Auto-waits do Playwright, sem clicks em elementos invisiveis      |

---

## 10. Conclusao

Este plano cobre **40 cenarios de teste** distribuidos em 3 pilares (Web, API, Performance) com foco adicional em seguranca e observabilidade. A arquitetura foi desenhada para ser extensivel, mantendo separacao clara entre modulos e facilitando a execucao tanto local quanto via CI/CD.
