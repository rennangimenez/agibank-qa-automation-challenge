# 📋 Plano de Testes — AgiBank QA Automation Challenge

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 10/04/2026
**Versão:** 3.0

---

## 📌 1. Objetivo

Este plano de testes documenta a estratégia completa de automação de testes para o desafio técnico de **QA Senior** do AgiBank, cobrindo três pilares: **Web**, **API** e **Performance**.

O objetivo é validar a **qualidade**, **confiabilidade** e **segurança** das aplicações alvo, demonstrando senioridade técnica em:

- 🏗️ Arquitetura de testes escalável com padrão Page Object + AAA
- 🔐 Testes de segurança ofensivos com documentação de findings reais
- 📊 Observabilidade com dashboards Grafana e métricas cumulativas
- 📸 Evidências automáticas em todos os cenários (sucesso e falha)
- 🔄 CI/CD automatizado com execução programada 2x/dia
- 🎨 Reports Allure enriquecidos com emojis, categorias e trends históricos

---

## 🎯 2. Escopo

### 2.1 Aplicações Alvo

|  #  | Aplicação      |   Tipo   | URL                      | Escopo                         |
| :-: | :------------- | :------: | :----------------------- | :----------------------------- |
|  1  | 🌐 Blog do Agi |   Web    | https://blogdoagi.com.br | Busca, navegação, segurança    |
|  2  | 🐕 Dog API     | REST API | https://dog.ceo/api      | Endpoints, contrato, segurança |
|  3  | ✈️ BlazeDemo   | Web App  | https://blazedemo.com    | Fluxo de compra sob carga      |

### 2.2 Tipos de Teste Cobertos

| Tipo            | Ícone | Descrição                                                                 | Módulos             |
| :-------------- | :---: | :------------------------------------------------------------------------ | :------------------ |
| Funcional (Web) |  🌐   | Busca, componentes UI, smoke tests                                        | `web-tests`         |
| Funcional (API) |  🐕   | Endpoints, contrato JSON Schema, edge cases                               | `api-tests`         |
| Segurança (Web) |  🔒   | SQL Injection, XSS, HTML Injection                                        | `web-tests`         |
| Segurança (API) |  🛡️   | SQL Injection, Path Traversal, Information Disclosure, Headers maliciosos | `api-tests`         |
| Performance     |  ✈️   | Carga sustentada (load) e pico (spike)                                    | `performance-tests` |

### 2.3 Fora de Escopo

- ❌ Testes de acessibilidade (WCAG)
- ❌ Testes cross-browser (foco em Chromium)
- ❌ Testes de integração entre as aplicações alvo
- ❌ Testes de responsividade mobile

---

## 🏗️ 3. Stack Tecnológica

|      Camada      | Tecnologia         | Versão | Propósito                          |
| :--------------: | :----------------- | :----: | :--------------------------------- |
|   💻 Linguagem   | Java               |   17   | Linguagem principal                |
|     📦 Build     | Maven              | 3.9.6  | Multi-module com Maven Wrapper     |
|  🧪 Test Runner  | JUnit 5            | 5.10.2 | Framework unificado                |
|      🌐 Web      | Playwright Java    | 1.44.0 | Automação com auto-waits e tracing |
|      🐕 API      | RestAssured        | 5.4.0  | HTTP client fluente                |
|  ✈️ Performance  | JMeter             | 5.6.3  | Carga e pico                       |
|   📊 Reporting   | Allure             | 2.25.0 | Reports interativos com evidências |
| 📈 Observability | Grafana + InfluxDB | Latest | Dashboards tempo real              |
|     🔄 CI/CD     | GitHub Actions     |   —    | Self-hosted runner VPS             |
| 🧹 Code Quality  | Spotless + Husky   |   —    | Formatação e pre-commit            |

---

## 📸 4. Estratégia de Evidências

### 4.1 Evidências Automáticas (Web Tests)

Todos os testes web capturam evidências automaticamente via `ScreenshotOnResultExtension` (JUnit 5 `AfterTestExecutionCallback`):

| Evidência               | Quando          | Formato            |
| :---------------------- | :-------------- | :----------------- |
| 📸 Screenshot (Success) | ✅ Teste passou | PNG full-page      |
| 📸 Screenshot (Failure) | ❌ Teste falhou | PNG full-page      |
| 🔗 URL Final            | ✅ Sempre       | Texto              |
| 📄 Título da Página     | ✅ Sempre       | Texto              |
| 🎬 Playwright Trace     | ❌ Apenas falha | ZIP (debug visual) |

**Implementação técnica:**

- `BaseTest` inicia tracing via `context.tracing().start()` no `@BeforeEach`
- `Page` e status de tracing são passados via `ThreadLocal` para a extensão
- `ScreenshotOnResultExtension` captura evidências **antes** do `@AfterEach` (browser ainda ativo)

### 4.2 Evidências de API

- Allure RestAssured captura automaticamente request/response de cada chamada HTTP
- Logs de request e response anexados como attachments no report

### 4.3 Categorias Customizadas

Cada módulo possui `categories.json` definindo categorias para o Allure:

| Categoria                  | Tipo de Falha                                |
| :------------------------- | :------------------------------------------- |
| 🐛 Defeitos de Produto     | Erros de assertion (bug real na aplicação)   |
| 🔍 Elemento Não Encontrado | Locators Playwright falharam (web)           |
| 📄 Falhas de Schema        | Contrato JSON Schema violado (api)           |
| 🧪 Defeitos de Teste       | Erros no código de teste (NullPointer, etc.) |
| 🌐 Problemas de Rede       | Timeout de conexão, DNS, ERR_CONNECTION      |
| ⏱️ Erros de Timeout        | Timeout de espera excedido                   |
| ⏭️ Testes Desabilitados    | Skipped ou `@Disabled`                       |

### 4.4 Metadados de Ambiente

Gerados dinamicamente no CI para cada execução:

**`environment.properties`:**

- Java version, Allure version, framework (Playwright/RestAssured)
- Browser e viewport (web), Base URL
- Branch, commit SHA, runner name

**`executor.json`:**

- Build name/URL linkando direto para o GitHub Actions run
- Report URL para o report publicado

---

## 🧪 5. Casos de Teste

### 5.1 🌐 Web Tests — Blog do Agi (14 testes)

#### 5.1.1 💨 Smoke Tests (BlogSmokeTest) — 6 testes

|   ID    | Cenário                                                               | Prioridade  | Story                     |
| :-----: | :-------------------------------------------------------------------- | :---------: | :------------------------ |
| WEB-001 | 🏠 Página inicial carrega com sucesso (HTTP 200, URL correta, título) | 🔴 Blocker  | 🏠 Carregamento da Página |
| WEB-002 | 📌 Menu de navegação principal está visível                           | 🔴 Critical | 🏗️ Estrutura da Página    |
| WEB-003 | 🎨 Logo está presente no header                                       |  🟡 Normal  | 🏗️ Estrutura da Página    |
| WEB-004 | 📎 Footer está presente na página                                     |  🟡 Normal  | 🏗️ Estrutura da Página    |
| WEB-005 | 📰 Página exibe artigos/posts                                         | 🔴 Critical | 📝 Conteúdo               |
| WEB-006 | 🐛 Sem erros críticos de JavaScript no carregamento                   |  🟡 Normal  | 🏠 Carregamento da Página |

#### 5.1.2 🔍 Busca (BlogSearchTest) — 3 testes

|   ID    | Cenário                                                | Prioridade  | Story                       |
| :-----: | :----------------------------------------------------- | :---------: | :-------------------------- |
| WEB-007 | ✅ Busca com termo válido retorna resultados           | 🔴 Critical | ✅ Busca Válida             |
| WEB-008 | 🚫 Busca com termo inexistente exibe 'nada encontrado' |  🟡 Normal  | 🚫 Resultado Vazio          |
| WEB-009 | 🧩 Componentes de busca estão presentes na página      |  🟡 Normal  | 🧩 Componentes de Interface |

#### 5.1.3 🛡️ Segurança (BlogSecurityTest) — 5 testes

|   ID    | Cenário                                             | Prioridade  | Story                 | Payload                         |
| :-----: | :-------------------------------------------------- | :---------: | :-------------------- | :------------------------------ |
| WEB-010 | 💉 SQL Injection no campo de busca                  | 🔴 Critical | 💉 SQL Injection      | `'; DROP TABLE users;--`        |
| WEB-011 | 🚨 Tentativa de XSS não executa scripts             | 🔴 Critical | 🚨 Prevenção XSS      | `<script>alert('xss')</script>` |
| WEB-012 | ⚠️ **[ACHADO]** Injeção HTML renderiza no DOM       | 🔴 Critical | ⚠️ Injeção HTML       | `<h1>Injected</h1>`             |
| WEB-013 | 📏 Input extremamente longo não derruba a aplicação |  🟡 Normal  | 📏 Limites de Entrada | `"a".repeat(5000)`              |
| WEB-014 | 🔣 Caracteres especiais na busca são tratados       |  🟡 Normal  | 📏 Limites de Entrada | `<>&"'%\`                       |

---

### 5.2 🐕 API Tests — Dog API (26 testes)

#### 5.2.1 📋 Listagem de Raças (BreedListTest) — 4 testes

|   ID    | Cenário                                                     | Prioridade  | Story                     |
| :-----: | :---------------------------------------------------------- | :---------: | :------------------------ |
| API-001 | ✅ Listar todas as raças retorna 200                        | 🔴 Blocker  | 📋 Listar Todas as Raças  |
| API-002 | 🐶 Lista contém raças conhecidas (bulldog, labrador, hound) | 🔴 Critical | 📋 Listar Todas as Raças  |
| API-003 | 🔀 Sub-raças são retornadas como listas                     |  🟡 Normal  | 🔀 Estrutura de Sub-Raças |
| API-004 | 📄 Resposta segue o contrato JSON Schema                    | 🔴 Critical | 📄 Validação de Contrato  |

#### 5.2.2 🖼️ Imagens por Raça (BreedImagesTest) — 4 testes

|   ID    | Cenário                                                       | Prioridade  | Story                     |
| :-----: | :------------------------------------------------------------ | :---------: | :------------------------ |
| API-005 | ✅ Buscar imagens de raça válida retorna 200 com URLs         | 🔴 Critical | ✅ Imagens de Raça Válida |
| API-006 | 🔗 URLs das imagens são válidas e apontam para images.dog.ceo |  🟡 Normal  | ✅ Imagens de Raça Válida |
| API-007 | 🏷️ URLs das imagens contêm o nome da raça                     |  🟡 Normal  | ✅ Imagens de Raça Válida |
| API-008 | ❌ Raça inválida retorna 404                                  | 🔴 Critical | ❌ Raça Inválida          |

#### 5.2.3 🎲 Imagem Aleatória (RandomImageTest) — 3 testes

|   ID    | Cenário                                                 | Prioridade  | Story                     |
| :-----: | :------------------------------------------------------ | :---------: | :------------------------ |
| API-009 | ✅ Imagem aleatória retorna 200 com URL válida          | 🔴 Blocker  | 🎯 Imagem Aleatória Única |
| API-010 | 🖼️ URL da imagem tem extensão válida (.jpg, .png, etc.) |  🟡 Normal  | 🎯 Imagem Aleatória Única |
| API-011 | 📄 Resposta segue o contrato JSON Schema                | 🔴 Critical | 📄 Validação de Contrato  |

#### 5.2.4 ⚠️ Casos Extremos (EdgeCaseTest) — 9 testes

|   ID    | Cenário                                                    | Prioridade  | Story                           |
| :-----: | :--------------------------------------------------------- | :---------: | :------------------------------ |
| API-012 | 🔣 Nome de raça com caracteres especiais retorna 404       |  🟡 Normal  | 🚫 Nomes de Raça Inválidos      |
| API-013 | 🔢 Nome numérico de raça retorna 404                       |  🟡 Normal  | 🚫 Nomes de Raça Inválidos      |
| API-014 | 📏 Nome de raça extremamente longo retorna 404             |  🟡 Normal  | 🚫 Nomes de Raça Inválidos      |
| API-015 | 🌍 Nome de raça com caracteres Unicode é tratado           |  🟡 Normal  | 🚫 Nomes de Raça Inválidos      |
| API-016 | 🎲 Múltiplas imagens aleatórias retorna quantidade correta | 🔴 Critical | 🎲 Múltiplas Imagens Aleatórias |
| API-017 | 0️⃣ Requisição com contagem zero é tratada                  |  🟡 Normal  | 🎲 Múltiplas Imagens Aleatórias |
| API-018 | ➖ Contagem negativa não causa erro no servidor            |  🟡 Normal  | 🎲 Múltiplas Imagens Aleatórias |
| API-019 | 🐾 Imagens de sub-raça retornam resultados válidos         | 🔴 Critical | 🐾 Imagens de Sub-Raça          |
| API-020 | ❌ Sub-raça inválida retorna 404                           |  🟡 Normal  | 🐾 Imagens de Sub-Raça          |

#### 5.2.5 🛡️ Segurança (SecurityTest) — 6 testes

|   ID    | Cenário                                               | Prioridade  | Story                        | Payload                     |
| :-----: | :---------------------------------------------------- | :---------: | :--------------------------- | :-------------------------- |
| API-021 | 💉 SQL Injection no parâmetro de raça                 | 🔴 Critical | 💉 SQL Injection             | `' OR '1'='1`               |
| API-022 | 📂 Tentativa de Path Traversal é bloqueada            | 🔴 Critical | 📂 Path Traversal            | `../../../etc/passwd`       |
| API-023 | ⚠️ **[ACHADO]** API expõe tecnologia via X-Powered-By |  🟡 Normal  | 🔎 Divulgação de Informações | N/A (header check)          |
| API-024 | 🚨 Payload XSS não é refletido na resposta            | 🔴 Critical | 🚨 Prevenção XSS             | `<script>alert(1)</script>` |
| API-025 | 🧪 Content-Type malicioso é tratado com segurança     |  🟡 Normal  | 🧪 Headers Maliciosos        | XXE entity payload          |
| API-026 | 📏 Header de tamanho excessivo não derruba o servidor |  🟡 Normal  | 🧪 Headers Maliciosos        | `"A".repeat(8000)`          |

---

### 5.3 ✈️ Performance Tests — BlazeDemo (2 cenários)

#### 5.3.1 ⚡ Load Test (blazedemo-load-test.jmx)

|    ID    | Cenário                             | Config                              | Critério            |
| :------: | :---------------------------------- | :---------------------------------- | :------------------ |
| PERF-001 | Carga sustentada no fluxo de compra | 150 threads, ramp 60s, duração 240s | 250 req/s, p90 < 2s |

**Fluxo testado:**

1. 🏠 `GET /` — Home Page
2. 🔍 `POST /reserve.php` — Buscar voos
3. ✈️ `POST /purchase.php` — Selecionar voo
4. ✅ `POST /confirmation.php` — Confirmar compra

#### 5.3.2 📈 Spike Test (blazedemo-spike-test.jmx)

|    ID    | Cenário                      | Config                                                | Critério              |
| :------: | :--------------------------- | :---------------------------------------------------- | :-------------------- |
| PERF-002 | Burst de tráfego com 3 fases | Fase 1: 30t/60s → Fase 2: 200t/120s → Fase 3: 30t/60s | Recuperação pós-spike |

---

## 🔐 6. Findings de Segurança

### 6.1 ⚠️ [WEB-012] Injeção HTML — Blog do Agi

| Campo             | Detalhe                                                      |
| :---------------- | :----------------------------------------------------------- |
| **Severidade**    | 🔴 Critical                                                  |
| **Tipo**          | HTML Injection (Reflected)                                   |
| **Localização**   | Campo de busca → página de resultados                        |
| **Payload**       | `<h1>Injected</h1>`                                          |
| **Comportamento** | Tag `<h1>` renderizada como elemento DOM real                |
| **Impacto**       | Injeção de conteúdo visual, potencial phishing               |
| **Recomendação**  | Sanitizar output do termo de busca no template de resultados |
| **Referência**    | OWASP — Injection Flaws                                      |

### 6.2 ⚠️ [API-023] Information Disclosure — Dog API

| Campo            | Detalhe                                                |
| :--------------- | :----------------------------------------------------- |
| **Severidade**   | 🟡 Normal                                              |
| **Tipo**         | Information Disclosure                                 |
| **Header**       | `X-Powered-By: PHP/8.3.29`                             |
| **Impacto**      | Expõe tecnologia e versão do backend                   |
| **Recomendação** | Remover ou suprimir o header `X-Powered-By` no php.ini |
| **Referência**   | OWASP — Information Disclosure                         |

---

## 🏗️ 7. Padrão AAA (Arrange-Act-Assert)

Todos os testes seguem o padrão **AAA híbrido** com camadas de suporte:

```
Arrange → Dados vêm dos fixtures/ (SearchData, BreedData, SecurityPayloads)
Act     → Ações executadas via pages/ (Web) ou client/ (API)
Assert  → Validações explícitas com assertAll() para múltiplas verificações
```

**Camadas do projeto:**

| Camada               | Web                                 | API                             | Responsabilidade                  |
| :------------------- | :---------------------------------- | :------------------------------ | :-------------------------------- |
| `fixtures/`          | `SearchData`, `ExpectedResults`     | `BreedData`, `SecurityPayloads` | Dados de teste centralizados      |
| `pages/` / `client/` | `BlogHomePage`, `SearchResultsPage` | `DogApiClient`                  | Ações (Act)                       |
| `extensions/`        | `ScreenshotOnResultExtension`       | —                               | Captura automática de evidências  |
| `tests/`             | `BlogSearchTest`, etc.              | `BreedListTest`, etc.           | Orquestração AAA                  |
| `base/`              | `BaseTest`                          | —                               | Lifecycle do Playwright + tracing |

---

## 🔄 8. Estratégia de Execução

### 8.1 Execução Local

```bash
# 🐕 API Tests
./mvnw clean test -pl api-tests

# 🌐 Web Tests
./mvnw clean test -pl web-tests

# ✈️ Performance — Load Test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-load-test

# ✈️ Performance — Spike Test
./mvnw clean verify -pl performance-tests -Djmeter.test=blazedemo-spike-test
```

### 8.2 Execução CI/CD (GitHub Actions)

| Workflow          | Trigger        | Schedule (BRT) | Descrição                                          |
| :---------------- | :------------- | :------------- | :------------------------------------------------- |
| `ci.yml`          | Push/PR + Cron | 🕐 08h e 16h   | API + Web tests, Allure reports, métricas InfluxDB |
| `performance.yml` | Manual + Cron  | 🕐 00h         | JMeter test plan, HTML report, métricas pipeline   |

### 8.3 Comparação Local vs CI/CD

| Aspecto        | Local                         | CI/CD (VPS)                           |
| :------------- | :---------------------------- | :------------------------------------ |
| **Ambiente**   | Windows 11 / rede residencial | Ubuntu / VPS dedicada                 |
| **Latência**   | Variável (ISP)                | Estável (datacenter)                  |
| **Execução**   | Manual (`./mvnw`)             | Automática (push/cron)                |
| **Reports**    | Terminal / Allure local       | Allure online + Grafana               |
| **Evidências** | Screenshots locais            | Screenshots + traces no report online |
| **Métricas**   | Nenhuma                       | InfluxDB + 3 dashboards Grafana       |
| **Reprodução** | Depende do setup local        | 100% reproduzível (runner fixo)       |

---

## 📈 9. Observabilidade

### 9.1 Stack

```
GitHub Actions → Testes → Allure/JMeter
                              │
            push-allure-metrics.sh ──→ InfluxDB ──→ Grafana
            push-pipeline-metrics.sh ↗
            JMeter Backend Listener ↗
```

### 9.2 Dashboards Grafana

| Dashboard               | Fonte de Dados                                                | Métricas                                           |
| :---------------------- | :------------------------------------------------------------ | :------------------------------------------------- |
| 📊 Quality Overview     | `test_results`, `test_severity`, `test_executions_cumulative` | Pass rate Web/API, trends, severidade, cumulativos |
| ✈️ Performance (JMeter) | `jmeter`                                                      | Latência (avg/p90/p95/p99), throughput, error rate |
| 🔄 Pipeline Health      | `pipeline_runs`                                               | Success rate, duração por job, feedback time       |

**URL:** https://rennangimenez.com/grafana/

### 9.3 Métricas Coletadas

| Série InfluxDB               | Métricas                                                       | Quando        |
| :--------------------------- | :------------------------------------------------------------- | :------------ |
| `test_results`               | total, passed, failed, broken, skipped, duration, avg_duration | A cada CI run |
| `test_severity`              | total, passed, failed por nível de severidade                  | A cada CI run |
| `test_executions_cumulative` | total_runs, total_tests, total_passed, total_failed            | A cada CI run |
| `pipeline_runs`              | duração, status, job name                                      | A cada CI run |
| `jmeter`                     | latência, throughput, errors por transação                     | Em tempo real |

---

## 📊 10. Reports e Evidências

| Report                  | Formato         | URL                                                      |
| :---------------------- | :-------------- | :------------------------------------------------------- |
| 🌐 Allure — Web Tests   | HTML interativo | https://rennangimenez.com/agibank-challenge/web/         |
| 🐕 Allure — API Tests   | HTML interativo | https://rennangimenez.com/agibank-challenge/api/         |
| ✈️ JMeter — Performance | HTML            | https://rennangimenez.com/agibank-challenge/performance/ |
| 📈 Grafana — Dashboards | Web App         | https://rennangimenez.com/grafana/                       |

### Features dos Reports Allure

| Feature                    | Descrição                                               |
| :------------------------- | :------------------------------------------------------ |
| 📸 Screenshots automáticas | Captura full-page em sucesso e falha                    |
| 🎬 Playwright Traces       | Arquivo ZIP para debug visual (apenas falhas)           |
| 🔗 Metadados de navegação  | URL final + título da página anexados                   |
| 📂 Categorias customizadas | Classificação automática de falhas por tipo             |
| 🌍 Environment info        | Java, browser, OS, branch, commit, runner               |
| 📈 Trends históricos       | Gráficos nativos de evolução (history preservado no CI) |
| 🎨 Nomes amigáveis         | Suites, testes e stories com emojis em PT-BR            |
| 🏷️ Anotações ricas         | @Epic, @Feature, @Story, @Owner, @Severity, @Link       |

---

## ⚠️ 11. Riscos e Mitigações

| Risco                       |  Prob.   | Impacto  | Mitigação                                     |
| :-------------------------- | :------: | :------: | :-------------------------------------------- |
| Blog muda domínio/estrutura | 🟡 Média | 🔴 Alto  | URL navigation direta, locators semânticos    |
| Dog API indisponível        | 🟢 Baixa | 🔴 Alto  | Testes falham gracefully, CI tem timeout      |
| BlazeDemo fora do ar        | 🟢 Baixa | 🔴 Alto  | Performance via workflow_dispatch + cron      |
| VPS runner offline          | 🟢 Baixa | 🔴 Alto  | Monitoramento Grafana + alertas               |
| Flakiness em testes web     | 🟡 Média | 🟡 Médio | Auto-waits Playwright, screenshots para debug |

---

## 🚀 12. Next Steps — 25 Novos Cenários Propostos

### 12.1 🌐 Web (10 cenários)

|    ID     | Cenário Proposto                                   | Tipo           | Justificativa       |
| :-------: | :------------------------------------------------- | :------------- | :------------------ |
| WEB-NS-01 | Validar breadcrumbs na página de artigo            | Funcional      | Navegação e SEO     |
| WEB-NS-02 | Verificar tags/categorias nos posts                | Funcional      | Taxonomia do blog   |
| WEB-NS-03 | Testar paginação da home page                      | Funcional      | UX em listas longas |
| WEB-NS-04 | Validar compartilhamento social (botões presentes) | Funcional      | Engajamento         |
| WEB-NS-05 | Testar responsividade mobile (viewport 375px)      | UI/UX          | Mobile first        |
| WEB-NS-06 | Verificar metatags Open Graph para SEO             | SEO            | Social sharing      |
| WEB-NS-07 | Testar navegação por teclado (Tab + Enter)         | Acessibilidade | WCAG básico         |
| WEB-NS-08 | Injeção de iframe via busca                        | Segurança      | Iframe injection    |
| WEB-NS-09 | Testar busca com emojis e unicode                  | Edge Case      | Internacionalização |
| WEB-NS-10 | Performance audit com Lighthouse (via Playwright)  | Performance    | Core Web Vitals     |

### 12.2 🐕 API (10 cenários)

|    ID     | Cenário Proposto                                        | Tipo        | Justificativa      |
| :-------: | :------------------------------------------------------ | :---------- | :----------------- |
| API-NS-01 | Validar CORS headers na response                        | Segurança   | Cross-origin       |
| API-NS-02 | Testar rate limiting (burst de requests)                | Performance | Resiliência        |
| API-NS-03 | Validar Content-Type correto (application/json)         | Contrato    | Consistência       |
| API-NS-04 | Testar cache headers (ETag, Cache-Control)              | Performance | Eficiência         |
| API-NS-05 | Validar que métodos não suportados retornam 405         | Contrato    | RESTful compliance |
| API-NS-06 | Testar concurrent requests ao mesmo endpoint            | Performance | Thread safety      |
| API-NS-07 | Validar idempotência de GETs consecutivos               | Funcional   | Consistência       |
| API-NS-08 | Testar encoding URL com caracteres reservados           | Edge Case   | RFC compliance     |
| API-NS-09 | Validar response time SLA (< 500ms para lista de raças) | Performance | SLA                |
| API-NS-10 | Command injection via breed parameter                   | Segurança   | OS injection       |

### 12.3 ✈️ Performance (5 cenários)

|     ID     | Cenário Proposto                                           | Tipo      | Justificativa       |
| :--------: | :--------------------------------------------------------- | :-------- | :------------------ |
| PERF-NS-01 | Stress test — aumentar threads até quebrar                 | Stress    | Limite do sistema   |
| PERF-NS-02 | Endurance test — carga baixa por 1 hora                    | Endurance | Memory leaks        |
| PERF-NS-03 | Teste de carga somente no endpoint de confirmação          | Isolado   | Bottleneck analysis |
| PERF-NS-04 | Teste com think time zero (worst case)                     | Stress    | Throughput máximo   |
| PERF-NS-05 | Comparativo de performance entre períodos (manhã vs noite) | Baseline  | Variação temporal   |

---

## 📌 13. Conclusão

Este plano cobre **42 cenários de teste implementados** (14 web + 26 api + 2 performance) distribuídos em 3 pilares com foco adicional em segurança e observabilidade.

**Destaques da arquitetura:**

| Aspecto             | Implementação                                                |
| :------------------ | :----------------------------------------------------------- |
| 🏗️ Padrão de design | Page Object Model + AAA híbrido com fixtures                 |
| 📸 Evidências       | Screenshots automáticas em todos os testes (sucesso e falha) |
| 🎨 Reports          | Allure com emojis, categorias, environment info e trends     |
| 📈 Métricas         | 5 séries InfluxDB alimentando 3 dashboards Grafana           |
| 🔐 Segurança        | 2 findings reais documentados como assertions positivas      |
| 🔄 CI/CD            | Execução programada 2x/dia com deploy automático             |

A execução é automatizada via CI/CD com schedule programado e métricas em tempo real via Grafana, proporcionando visibilidade completa do estado de qualidade do projeto.

**25 novos cenários propostos** como próximos passos demonstram a visão de evolução contínua da suíte de testes.
