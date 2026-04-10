# 🧪 Relatório de Execução de Testes — Web + API

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 10/04/2026
**Versão:** 2.0

---

## 📌 1. Objetivo

Documentar os resultados de execução dos testes automatizados de **Web** (Blog do Agi) e **API** (Dog API), incluindo métricas de qualidade, findings de segurança, estratégia de evidências e análise de resultados.

Este relatório complementa o [Plano de Testes](test-plan.md) e o [Relatório de Performance](performance-report.md).

---

## 📊 2. Resumo Executivo

### 2.1 Cobertura Geral

|   Pilar   | App Alvo    | Total Testes | Tipos                                     |
| :-------: | :---------- | :----------: | :---------------------------------------- |
|  🌐 Web   | Blog do Agi |      14      | Funcional, Smoke, Segurança               |
|  🐕 API   | Dog API     |      26      | Funcional, Contrato, Edge Case, Segurança |
| **Total** | —           |    **40**    | —                                         |

### 2.2 Resultado da Execução

|   Pilar   | ✅ Passed | ❌ Failed | ⏭️ Skipped | 📊 Pass Rate |
| :-------: | :-------: | :-------: | :--------: | :----------: |
|  🌐 Web   |    14     |     0     |     0      |   **100%**   |
|  🐕 API   |    26     |     0     |     0      |   **100%**   |
| **Total** |  **40**   |   **0**   |   **0**    | **100%** ✅  |

> 💡 Todos os 40 testes passam consistentemente tanto na execução local quanto na CI/CD. Os testes de segurança que documentam findings (WEB-012, API-023) foram escritos como assertions positivas que validam a presença da vulnerabilidade.

### 2.3 Findings de Segurança Identificados

|     ID     | Finding                                       | Severidade  | Aplicação   |
| :--------: | :-------------------------------------------- | :---------: | :---------- |
| ⚠️ WEB-012 | Injeção HTML renderiza como elemento DOM real | 🔴 Critical | Blog do Agi |
| ⚠️ API-023 | Header X-Powered-By expõe PHP/8.3.29          |  🟡 Normal  | Dog API     |

---

## 🌐 3. Resultados Web — Blog do Agi

### 3.1 Ambiente de Teste

| Aspecto       | Detalhe                                   |
| :------------ | :---------------------------------------- |
| 🌐 URL        | https://blogdoagi.com.br                  |
| 🖥️ Browser    | Chromium (headless)                       |
| 🔧 Framework  | Playwright Java 1.44.0                    |
| 📐 Viewport   | 1920x1080                                 |
| 🏗️ Padrão     | Page Object Model + AAA                   |
| 📸 Evidências | Screenshots automáticas (sucesso + falha) |
| 🎬 Traces     | Playwright Traces em falhas               |

### 3.2 💨 Smoke Tests (BlogSmokeTest) — 6/6 ✅

|   ID    | Cenário                                  | Resultado | Observação                                      |
| :-----: | :--------------------------------------- | :-------: | :---------------------------------------------- |
| WEB-001 | 🏠 Página inicial carrega com sucesso    |  ✅ Pass  | Header visível, URL correta, título presente    |
| WEB-002 | 📌 Menu de navegação principal visível   |  ✅ Pass  | `<nav>` renderizado corretamente                |
| WEB-003 | 🎨 Logo presente no header               |  ✅ Pass  | Detectado via `header img/svg`                  |
| WEB-004 | 📎 Footer visível na página              |  ✅ Pass  | Elemento `<footer>` visível                     |
| WEB-005 | 📰 Página exibe artigos/posts            |  ✅ Pass  | Múltiplos `<article>` encontrados               |
| WEB-006 | 🐛 Sem erros JS críticos no carregamento |  ✅ Pass  | Erros de recurso 404 filtrados (comuns em prod) |

### 3.3 🔍 Busca (BlogSearchTest) — 3/3 ✅

|   ID    | Cenário                                          | Resultado | Observação                                                      |
| :-----: | :----------------------------------------------- | :-------: | :-------------------------------------------------------------- |
| WEB-007 | ✅ Busca "empréstimo" retorna resultados         |  ✅ Pass  | 5 assertions validadas (URL, resultados, heading, título, link) |
| WEB-008 | 🚫 Busca "xyzqwerty999" mostra "nada encontrado" |  ✅ Pass  | Mensagem de no-results exibida corretamente                     |
| WEB-009 | 🧩 Componentes de busca presentes no DOM         |  ✅ Pass  | Ícone + form disponíveis                                        |

### 3.4 🛡️ Segurança (BlogSecurityTest) — 5/5 ✅

|   ID    | Cenário                            | Resultado |  Finding?  | Detalhes                                    |
| :-----: | :--------------------------------- | :-------: | :--------: | :------------------------------------------ |
| WEB-010 | 💉 SQL Injection no campo de busca |  ✅ Pass  |   ❌ Não   | Nenhum erro SQL exposto                     |
| WEB-011 | 🚨 Tentativa de XSS                |  ✅ Pass  |   ❌ Não   | Nenhum dialog JS disparado                  |
| WEB-012 | ⚠️ Injeção HTML renderiza no DOM   |  ✅ Pass  | ⚠️ **Sim** | `<h1>Injected</h1>` renderiza como DOM real |
| WEB-013 | 📏 Input longo (5.000 chars)       |  ✅ Pass  |   ❌ Não   | Página respondeu normalmente                |
| WEB-014 | 🔣 Caracteres especiais            |  ✅ Pass  |   ❌ Não   | Encoding correto aplicado                   |

---

## 🐕 4. Resultados API — Dog API

### 4.1 Ambiente de Teste

| Aspecto       | Detalhe                                                            |
| :------------ | :----------------------------------------------------------------- |
| 🐕 Base URL   | https://dog.ceo/api                                                |
| 🔧 Framework  | RestAssured 5.4.0                                                  |
| 📋 Validação  | JSON Schema + Status Codes + Body                                  |
| 🏗️ Padrão     | Client Layer + AAA                                                 |
| 📸 Evidências | Request/response capturados automaticamente via Allure RestAssured |

### 4.2 📋 Listagem de Raças (BreedListTest) — 4/4 ✅

|   ID    | Cenário                              | Resultado | Observação                            |
| :-----: | :----------------------------------- | :-------: | :------------------------------------ |
| API-001 | ✅ Listar todas as raças retorna 200 |  ✅ Pass  | Status "success" confirmado           |
| API-002 | 🐶 Contém bulldog, labrador, hound   |  ✅ Pass  | Todas raças conhecidas presentes      |
| API-003 | 🔀 Sub-raças são listas              |  ✅ Pass  | Todas entradas validadas              |
| API-004 | 📄 JSON Schema validation            |  ✅ Pass  | Conforma com `breed-list-schema.json` |

### 4.3 🖼️ Imagens por Raça (BreedImagesTest) — 4/4 ✅

|   ID    | Cenário                             | Resultado | Observação                     |
| :-----: | :---------------------------------- | :-------: | :----------------------------- |
| API-005 | ✅ Imagens de hound → 200 + URLs    |  ✅ Pass  | Lista não vazia                |
| API-006 | 🔗 URLs apontam para images.dog.ceo |  ✅ Pass  | Prefixo validado em todas URLs |
| API-007 | 🏷️ URLs contêm nome da raça         |  ✅ Pass  | "hound" presente no path       |
| API-008 | ❌ Raça inválida → 404              |  ✅ Pass  | Status "error" confirmado      |

### 4.4 🎲 Imagem Aleatória (RandomImageTest) — 3/3 ✅

|   ID    | Cenário                         | Resultado | Observação                                |
| :-----: | :------------------------------ | :-------: | :---------------------------------------- |
| API-009 | ✅ Imagem aleatória → 200 + URL |  ✅ Pass  | URL válida retornada                      |
| API-010 | 🖼️ Extensão de imagem válida    |  ✅ Pass  | jpg/jpeg/png/gif/webp                     |
| API-011 | 📄 JSON Schema validation       |  ✅ Pass  | Conforma com `image-response-schema.json` |

### 4.5 ⚠️ Casos Extremos (EdgeCaseTest) — 9/9 ✅

|   ID    | Cenário                       | Resultado | HTTP Code | Observação               |
| :-----: | :---------------------------- | :-------: | :-------: | :----------------------- |
| API-012 | 🔣 Caracteres especiais `@#$` |  ✅ Pass  |    404    | Error retornado          |
| API-013 | 🔢 Nome numérico `12345`      |  ✅ Pass  |    404    | —                        |
| API-014 | 📏 Nome longo (200 chars)     |  ✅ Pass  |  404/414  | Graceful handling        |
| API-015 | 🌍 Unicode `café☕🐕`         |  ✅ Pass  |   < 500   | Sem crash                |
| API-016 | 🎲 5 imagens aleatórias       |  ✅ Pass  |    200    | Contagem exata           |
| API-017 | 0️⃣ Count zero                 |  ✅ Pass  |  200/400  | Handling correto         |
| API-018 | ➖ Count negativo             |  ✅ Pass  |   < 500   | Sem crash                |
| API-019 | 🐾 Sub-raça hound/afghan      |  ✅ Pass  |    200    | Imagens com path correto |
| API-020 | ❌ Sub-raça inválida          |  ✅ Pass  |    404    | Error retornado          |

### 4.6 🛡️ Segurança (SecurityTest) — 6/6 ✅

|   ID    | Cenário                   | Resultado |  Finding?  | Detalhes                       |
| :-----: | :------------------------ | :-------: | :--------: | :----------------------------- |
| API-021 | 💉 SQL Injection          |  ✅ Pass  |   ❌ Não   | Nenhuma referência SQL exposta |
| API-022 | 📂 Path Traversal         |  ✅ Pass  |   ❌ Não   | Nenhum conteúdo de filesystem  |
| API-023 | ⚠️ X-Powered-By           |  ✅ Pass  | ⚠️ **Sim** | `PHP/8.3.29` exposto no header |
| API-024 | 🚨 XSS reflection         |  ✅ Pass  |   ❌ Não   | `<script>` não refletido       |
| API-025 | 🧪 Content-Type malicioso |  ✅ Pass  |   ❌ Não   | Sem 5xx                        |
| API-026 | 📏 Header oversized (8KB) |  ✅ Pass  |   ❌ Não   | Handling graceful              |

---

## 🔐 5. Findings de Segurança — Detalhamento

### 5.1 ⚠️ [WEB-012] Injeção HTML — Blog do Agi

| Campo                       | Detalhe                                                                   |
| :-------------------------- | :------------------------------------------------------------------------ |
| **Severidade**              | 🔴 Critical                                                               |
| **Tipo**                    | HTML Injection (Reflected)                                                |
| **Vetor**                   | Campo de busca → página de resultados                                     |
| **Payload**                 | `<h1>Injected</h1>`                                                       |
| **Comportamento observado** | A tag `<h1>` é renderizada como elemento DOM real na página de resultados |
| **Impacto**                 | Injeção de conteúdo visual arbitrário, potencial para phishing            |
| **Causa raiz**              | WordPress search template não sanitiza output do termo de busca           |
| **Recomendação**            | Aplicar `esc_html()` ou `sanitize_text_field()` no output do search term  |
| **Referência**              | OWASP — Injection Flaws                                                   |

### 5.2 ⚠️ [API-023] Information Disclosure — Dog API

| Campo                       | Detalhe                                                          |
| :-------------------------- | :--------------------------------------------------------------- |
| **Severidade**              | 🟡 Normal                                                        |
| **Tipo**                    | Information Disclosure                                           |
| **Header**                  | `X-Powered-By: PHP/8.3.29`                                       |
| **Comportamento observado** | Todos os endpoints retornam o header expondo tecnologia e versão |
| **Impacto**                 | Atacantes podem direcionar exploits específicos para PHP 8.3.29  |
| **Recomendação**            | `expose_php = Off` no `php.ini` ou remover via web server config |
| **Referência**              | OWASP — Information Disclosure                                   |

---

## 📸 6. Estratégia de Evidências

### 6.1 Evidências Capturadas (Web Tests)

Cada teste web captura automaticamente via `ScreenshotOnResultExtension`:

| Evidência           | Quando                      | Formato       | Attachment no Allure                             |
| :------------------ | :-------------------------- | :------------ | :----------------------------------------------- |
| 📸 Screenshot       | ✅ Sempre (sucesso e falha) | PNG full-page | `Screenshot (Success)` ou `Screenshot (Failure)` |
| 🔗 URL Final        | ✅ Sempre                   | Texto         | `Final URL`                                      |
| 📄 Título da Página | ✅ Sempre                   | Texto         | `Page Title`                                     |
| 🎬 Playwright Trace | ❌ Apenas em falhas         | ZIP           | `Playwright Trace`                               |

### 6.2 Evidências Capturadas (API Tests)

| Evidência            | Quando    | Fonte                     |
| :------------------- | :-------- | :------------------------ |
| 📤 Request completo  | ✅ Sempre | Allure RestAssured filter |
| 📥 Response completo | ✅ Sempre | Allure RestAssured filter |
| 📋 Headers           | ✅ Sempre | Captura automática        |

### 6.3 Categorias de Falha

Categorias customizadas definidas em `categories.json` para classificação automática:

| Categoria                  | Critério                     | Módulo    |
| :------------------------- | :--------------------------- | :-------- |
| 🐛 Defeitos de Produto     | Erros de assertion           | Web + API |
| 🔍 Elemento Não Encontrado | Locators Playwright falharam | Web       |
| 📄 Falhas de Schema        | Contrato JSON Schema violado | API       |
| 🧪 Defeitos de Teste       | NullPointer, ClassCast, etc. | Web + API |
| 🌐 Problemas de Rede       | Timeout de conexão, DNS      | Web + API |
| ⏱️ Erros de Timeout        | Timeout de espera excedido   | Web       |
| ⏭️ Testes Desabilitados    | Skipped ou `@Disabled`       | Web + API |

### 6.4 Metadados de Ambiente

Gerados dinamicamente em cada execução CI:

| Arquivo                  | Conteúdo                                                                     |
| :----------------------- | :--------------------------------------------------------------------------- |
| `environment.properties` | Java version, framework, browser, viewport, base URL, branch, commit, runner |
| `executor.json`          | Build name, build URL (link direto pro GitHub Actions), report URL           |

---

## 🏗️ 7. Arquitetura dos Testes

### 7.1 Padrão AAA Híbrido

```
┌─────────────────────────────────────────────────────┐
│  tests/            → Orquestração AAA               │
│    Arrange: dados via fixtures/                     │
│    Act: ações via pages/ ou client/                 │
│    Assert: validações com assertAll()               │
├─────────────────────────────────────────────────────┤
│  extensions/       → Evidências automáticas         │
│    ScreenshotOnResultExtension                      │
├─────────────────────────────────────────────────────┤
│  fixtures/         → Dados de teste                 │
│    SearchData, BreedData, SecurityPayloads          │
├─────────────────────────────────────────────────────┤
│  pages/ | client/  → Camada de ações                │
│    BlogHomePage, SearchResultsPage, DogApiClient    │
├─────────────────────────────────────────────────────┤
│  base/             → Lifecycle (Playwright + Trace) │
│    BaseTest                                         │
└─────────────────────────────────────────────────────┘
```

### 7.2 Fixtures

| Módulo | Fixture            | Conteúdo                                               |
| :----- | :----------------- | :----------------------------------------------------- |
| 🌐 Web | `SearchData`       | Termos de busca, payloads de segurança, input longo    |
| 🌐 Web | `ExpectedResults`  | Fragmentos de URL, seletores CSS                       |
| 🐕 API | `BreedData`        | Raças válidas/inválidas, contagens, URLs base          |
| 🐕 API | `SecurityPayloads` | SQL injection, path traversal, XSS, headers maliciosos |

---

## 📊 8. Métricas de Qualidade

### 8.1 Cobertura por Tipo de Teste

| Tipo      | 🌐 Web | 🐕 API | Total  |    %     |
| :-------- | :----: | :----: | :----: | :------: |
| Funcional |   3    |   12   |   15   |  37.5%   |
| Smoke     |   6    |   —    |   6    |   15%    |
| Segurança |   5    |   6    |   11   |  27.5%   |
| Contrato  |   —    |   2    |   2    |    5%    |
| Edge Case |   —    |   6    |   6    |   15%    |
| **Total** | **14** | **26** | **40** | **100%** |

### 8.2 Cobertura por Severidade

| Severidade  |  Qtd   |    %     |
| :---------- | :----: | :------: |
| 🔴 Blocker  |   3    |   7.5%   |
| 🔴 Critical |   16   |   40%    |
| 🟡 Normal   |   21   |  52.5%   |
| **Total**   | **40** | **100%** |

### 8.3 Estabilidade

| Métrica                             | Valor                                      |
| :---------------------------------- | :----------------------------------------- |
| ✅ Execuções consecutivas sem falha | > 10                                       |
| 📊 Flaky rate                       | 0%                                         |
| 🌐 Ambiente consistente             | Local + CI/CD                              |
| 📈 Trends históricos                | Disponíveis no Allure (history preservado) |

---

## 🔄 9. Execução CI/CD

### 9.1 Pipeline

| Workflow | Trigger           | Schedule         | Jobs                                                 |
| :------- | :---------------- | :--------------- | :--------------------------------------------------- |
| `ci.yml` | Push/PR para main | 🕐 08h e 16h BRT | deploy-monitoring → api-tests + web-tests (paralelo) |

### 9.2 Artefatos Gerados

| Artefato               | Destino                                                       | URL                                              |
| :--------------------- | :------------------------------------------------------------ | :----------------------------------------------- |
| 🌐 Allure Report (Web) | `/srv/apps/agibank-challenge-reports/web/`                    | https://rennangimenez.com/agibank-challenge/web/ |
| 🐕 Allure Report (API) | `/srv/apps/agibank-challenge-reports/api/`                    | https://rennangimenez.com/agibank-challenge/api/ |
| 📊 Métricas InfluxDB   | `test_results`, `test_severity`, `test_executions_cumulative` | Grafana Quality Overview                         |

### 9.3 Métricas de Pipeline

| Métrica                      | Valor                             |
| :--------------------------- | :-------------------------------- |
| ⏱️ Duração média (API tests) | ~60-90s                           |
| ⏱️ Duração média (Web tests) | ~120-180s                         |
| 📊 Feedback time total       | < 5 min                           |
| 📈 Histórico                 | Grafana Pipeline Health dashboard |

---

## 📎 10. Evidências e Links

| Evidência                     | Tipo                                        | URL                                                                      |
| :---------------------------- | :------------------------------------------ | :----------------------------------------------------------------------- |
| 🌐 Allure Report — Web        | HTML interativo com screenshots             | https://rennangimenez.com/agibank-challenge/web/                         |
| 🐕 Allure Report — API        | HTML interativo com request/response        | https://rennangimenez.com/agibank-challenge/api/                         |
| 📊 Grafana — Quality Overview | Dashboard com métricas cumulativas e trends | https://rennangimenez.com/grafana/                                       |
| 🔄 Grafana — Pipeline Health  | Dashboard com CI/CD metrics                 | https://rennangimenez.com/grafana/                                       |
| 🔄 GitHub Actions             | Logs de execução                            | https://github.com/rennangimenez/agibank-qa-automation-challenge/actions |

---

## 📌 11. Conclusão

A suíte de testes Web + API apresenta **100% de pass rate** com **40 cenários automatizados** cobrindo funcionalidades, contratos, edge cases e segurança.

| Destaque                                     | Status                                       |
| :------------------------------------------- | :------------------------------------------- |
| ✅ 40/40 testes passando                     | Estabilidade total                           |
| ⚠️ 2 findings de segurança documentados      | HTML Injection + Info Disclosure             |
| 📸 Evidências automáticas em todos os testes | Screenshots + URL + título (sucesso e falha) |
| 🎬 Playwright Traces em falhas               | Debug visual passo-a-passo                   |
| 📂 Categorias customizadas                   | Classificação automática de falhas           |
| 🌍 Environment info no report                | Java, browser, OS, branch, commit            |
| 📈 Trends históricos                         | Gráficos nativos de evolução                 |
| 🎨 Nomes amigáveis com emojis                | Suites e testes em PT-BR                     |
| ✅ Padrão AAA com fixtures                   | Separação de responsabilidades               |
| ✅ CI/CD com execução programada             | 2x/dia (08h e 16h BRT)                       |
| ✅ Métricas em tempo real                    | 5 séries InfluxDB → 3 dashboards Grafana     |
| ✅ 0% flaky rate                             | Estabilidade comprovada                      |

A execução automatizada garante monitoramento contínuo da qualidade, com visibilidade completa via dashboards Grafana e reports Allure online enriquecidos com evidências visuais, categorias e trends.
