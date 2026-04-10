# 🧪 Relatorio de Execucao de Testes — Web + API

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 09/04/2026
**Versao:** 1.0

---

## 📌 1. Objetivo

Documentar os resultados de execucao dos testes automatizados de **Web** (Blog do Agi) e **API** (Dog API), incluindo metricas de qualidade, findings de seguranca e analise de resultados. Este relatorio complementa o **Plano de Testes** e o **Relatorio de Performance**.

---

## 📊 2. Resumo Visual

### 2.1 Cobertura Geral

| Pilar     | App Alvo    | Total Testes | Tipos                                     |
| --------- | ----------- | ------------ | ----------------------------------------- |
| 🌐 Web    | Blog do Agi | 14           | Funcional, Smoke, Seguranca               |
| 🔗 API    | Dog API     | 26           | Funcional, Contrato, Edge Case, Seguranca |
| **Total** | —           | **40**       | —                                         |

### 2.2 Resultado da Execucao

| Pilar     | ✅ Passed | ❌ Failed | ⏭️ Skipped | 📊 Pass Rate |
| --------- | --------- | --------- | ---------- | ------------ |
| 🌐 Web    | 14        | 0         | 0          | 100%         |
| 🔗 API    | 26        | 0         | 0          | 100%         |
| **Total** | **40**    | **0**     | **0**      | **100%**     |

> 💡 Todos os 40 testes passam consistentemente tanto na execucao local quanto na CI/CD. Os testes de seguranca que documentam findings (WEB-012, API-023) foram escritos como assertions positivas que validam a presenca da vulnerabilidade.

---

## 🌐 3. Resultados Web — Blog do Agi

### 3.1 Ambiente de Teste

| Aspecto      | Detalhe                  |
| ------------ | ------------------------ |
| 🌐 URL       | https://blogdoagi.com.br |
| 🖥️ Browser   | Chromium (headless)      |
| 🔧 Framework | Playwright Java 1.44.0   |
| 📐 Viewport  | 1920x1080                |
| 🏗️ Padrao    | Page Object Model + AAA  |

### 3.2 Search Tests (BlogSearchTest) — 3/3 ✅

| ID      | Cenario                                       | Resultado | Observacao                                                      |
| ------- | --------------------------------------------- | --------- | --------------------------------------------------------------- |
| WEB-001 | Busca "emprestimo" retorna resultados         | ✅ Pass   | 5 assertions validadas (URL, resultados, heading, titulo, link) |
| WEB-002 | Busca "xyzqwerty999" mostra "nada encontrado" | ✅ Pass   | Mensagem de no-results exibida corretamente                     |
| WEB-003 | Componentes de busca presentes no DOM         | ✅ Pass   | Icone + form disponíveis                                        |

### 3.3 Smoke Tests (BlogSmokeTest) — 6/6 ✅

| ID      | Cenario                         | Resultado | Observacao                                   |
| ------- | ------------------------------- | --------- | -------------------------------------------- |
| WEB-004 | Home page carrega com sucesso   | ✅ Pass   | Header visivel, URL correta, titulo presente |
| WEB-005 | Navegacao `<nav>` visivel       | ✅ Pass   | —                                            |
| WEB-006 | Logo presente no header         | ✅ Pass   | Detectado via `header img/svg`               |
| WEB-007 | Footer visivel                  | ✅ Pass   | —                                            |
| WEB-008 | Ao menos 1 `<article>` presente | ✅ Pass   | Multiplos artigos encontrados                |
| WEB-009 | Sem erros JS criticos           | ✅ Pass   | Erros de recurso 404 filtrados               |

### 3.4 Security Tests (BlogSecurityTest) — 5/5 ✅

| ID      | Cenario                  | Resultado | Finding?   | Detalhes                                    |
| ------- | ------------------------ | --------- | ---------- | ------------------------------------------- |
| WEB-010 | SQL Injection            | ✅ Pass   | Nao        | Nenhum erro SQL exposto                     |
| WEB-011 | XSS `<script>`           | ✅ Pass   | Nao        | Nenhum dialog JS disparado                  |
| WEB-012 | HTML Injection           | ✅ Pass   | ⚠️ **Sim** | `<h1>Injected</h1>` renderiza como DOM real |
| WEB-013 | Input longo (5000 chars) | ✅ Pass   | Nao        | Pagina respondeu normalmente                |
| WEB-014 | Caracteres especiais     | ✅ Pass   | Nao        | Encoding correto aplicado                   |

---

## 🔗 4. Resultados API — Dog API

### 4.1 Ambiente de Teste

| Aspecto      | Detalhe                           |
| ------------ | --------------------------------- |
| 🔗 Base URL  | https://dog.ceo/api               |
| 🔧 Framework | RestAssured 5.4.0                 |
| 📋 Validacao | JSON Schema + Status Codes + Body |
| 🏗️ Padrao    | Client Layer + AAA                |

### 4.2 Breed List (BreedListTest) — 4/4 ✅

| ID      | Cenario                         | Resultado | Observacao                            |
| ------- | ------------------------------- | --------- | ------------------------------------- |
| API-001 | GET `/breeds/list/all` → 200    | ✅ Pass   | Status "success" confirmado           |
| API-002 | Contem bulldog, labrador, hound | ✅ Pass   | Todas racas conhecidas presentes      |
| API-003 | Sub-racas sao arrays            | ✅ Pass   | Todas entradas validadas              |
| API-004 | JSON Schema validation          | ✅ Pass   | Conforma com `breed-list-schema.json` |

### 4.3 Breed Images (BreedImagesTest) — 4/4 ✅

| ID      | Cenario                          | Resultado | Observacao                     |
| ------- | -------------------------------- | --------- | ------------------------------ |
| API-005 | Imagens de hound → 200 + URLs    | ✅ Pass   | Lista nao vazia                |
| API-006 | URLs apontam para images.dog.ceo | ✅ Pass   | Prefixo validado em todas URLs |
| API-007 | URLs contem nome da raca         | ✅ Pass   | "hound" presente no path       |
| API-008 | Raca invalida → 404              | ✅ Pass   | Status "error" confirmado      |

### 4.4 Random Image (RandomImageTest) — 3/3 ✅

| ID      | Cenario                   | Resultado | Observacao                                |
| ------- | ------------------------- | --------- | ----------------------------------------- |
| API-009 | Random image → 200 + URL  | ✅ Pass   | URL valida retornada                      |
| API-010 | Extensao de imagem valida | ✅ Pass   | jpg/jpeg/png/gif/webp                     |
| API-011 | JSON Schema validation    | ✅ Pass   | Conforma com `image-response-schema.json` |

### 4.5 Edge Cases (EdgeCaseTest) — 9/9 ✅

| ID      | Cenario                    | Resultado | HTTP Code | Observacao               |
| ------- | -------------------------- | --------- | --------- | ------------------------ |
| API-012 | Caracteres especiais `@#$` | ✅ Pass   | 404       | Error retornado          |
| API-013 | Nome numerico `12345`      | ✅ Pass   | 404       | —                        |
| API-014 | Nome longo (200 chars)     | ✅ Pass   | 404/414   | Graceful handling        |
| API-015 | Unicode `cafe☕🐕`         | ✅ Pass   | < 500     | Sem crash                |
| API-016 | 5 imagens aleatorias       | ✅ Pass   | 200       | Contagem exata           |
| API-017 | Count zero                 | ✅ Pass   | 200/400   | Handling correto         |
| API-018 | Count negativo             | ✅ Pass   | < 500     | Sem crash                |
| API-019 | Sub-raca hound/afghan      | ✅ Pass   | 200       | Imagens com path correto |
| API-020 | Sub-raca invalida          | ✅ Pass   | 404       | Error retornado          |

### 4.6 Security Tests (SecurityTest) — 6/6 ✅

| ID      | Cenario                | Resultado | Finding?   | Detalhes                       |
| ------- | ---------------------- | --------- | ---------- | ------------------------------ |
| API-021 | SQL Injection          | ✅ Pass   | Nao        | Nenhuma referencia SQL exposta |
| API-022 | Path Traversal         | ✅ Pass   | Nao        | Nenhum conteudo de filesystem  |
| API-023 | X-Powered-By           | ✅ Pass   | ⚠️ **Sim** | `PHP/8.3.29` exposto no header |
| API-024 | XSS reflection         | ✅ Pass   | Nao        | `<script>` nao refletido       |
| API-025 | Content-Type malicioso | ✅ Pass   | Nao        | Sem 5xx                        |
| API-026 | Header oversized (8KB) | ✅ Pass   | Nao        | Handling graceful              |

---

## 🔐 5. Findings de Seguranca

### 5.1 ⚠️ [WEB-012] HTML Injection — Blog do Agi

| Campo                       | Detalhe                                                                   |
| --------------------------- | ------------------------------------------------------------------------- |
| **Severidade**              | 🔴 Critical                                                               |
| **Tipo**                    | HTML Injection (Reflected)                                                |
| **Vetor**                   | Campo de busca → pagina de resultados                                     |
| **Payload**                 | `<h1>Injected</h1>`                                                       |
| **Comportamento observado** | A tag `<h1>` e renderizada como elemento DOM real na pagina de resultados |
| **Impacto**                 | Injecao de conteudo visual arbitrario, potencial para phishing            |
| **Causa raiz**              | WordPress search template nao sanitiza output do termo de busca           |
| **Recomendacao**            | Aplicar `esc_html()` ou `sanitize_text_field()` no output do search term  |
| **Referencia**              | OWASP — Injection Flaws                                                   |

### 5.2 ⚠️ [API-023] Information Disclosure — Dog API

| Campo                       | Detalhe                                                          |
| --------------------------- | ---------------------------------------------------------------- |
| **Severidade**              | 🟡 Normal                                                        |
| **Tipo**                    | Information Disclosure                                           |
| **Header**                  | `X-Powered-By: PHP/8.3.29`                                       |
| **Comportamento observado** | Todos os endpoints retornam o header expondo tecnologia e versao |
| **Impacto**                 | Atacantes podem direcionar exploits especificos para PHP 8.3.29  |
| **Recomendacao**            | `expose_php = Off` no `php.ini` ou remover via web server config |
| **Referencia**              | OWASP — Information Disclosure                                   |

---

## 🏗️ 6. Arquitetura dos Testes

### 6.1 Padrao AAA Hibrido

Todos os testes seguem o padrao **Arrange-Act-Assert** com camadas de suporte:

```
┌─────────────────────────────────────────────┐
│  tests/          → Orquestracao AAA         │
│    Arrange: dados via fixtures/             │
│    Act: acoes via pages/ ou client/         │
│    Assert: validacoes com assertAll()       │
├─────────────────────────────────────────────┤
│  fixtures/       → Dados de teste           │
│    SearchData, BreedData, SecurityPayloads  │
├─────────────────────────────────────────────┤
│  pages/ | client/ → Camada de acoes         │
│    BlogHomePage, SearchResultsPage          │
│    DogApiClient                             │
├─────────────────────────────────────────────┤
│  base/           → Lifecycle (Playwright)   │
│    BaseTest                                 │
└─────────────────────────────────────────────┘
```

### 6.2 Fixtures

| Modulo | Fixture            | Conteudo                                               |
| ------ | ------------------ | ------------------------------------------------------ |
| Web    | `SearchData`       | Termos de busca, payloads de seguranca, input longo    |
| Web    | `ExpectedResults`  | Fragmentos de URL, seletores CSS                       |
| API    | `BreedData`        | Racas validas/invalidas, contagens, URLs base          |
| API    | `SecurityPayloads` | SQL injection, path traversal, XSS, headers maliciosos |

---

## 📊 7. Metricas de Qualidade

### 7.1 Cobertura por Tipo de Teste

| Tipo      | Web    | API    | Total  | %        |
| --------- | ------ | ------ | ------ | -------- |
| Funcional | 3      | 12     | 15     | 37.5%    |
| Smoke     | 6      | —      | 6      | 15%      |
| Seguranca | 5      | 6      | 11     | 27.5%    |
| Contrato  | —      | 2      | 2      | 5%       |
| Edge Case | —      | 6      | 6      | 15%      |
| **Total** | **14** | **26** | **40** | **100%** |

### 7.2 Cobertura por Severidade

| Severidade  | Qtd    | %        |
| ----------- | ------ | -------- |
| 🔴 Blocker  | 3      | 7.5%     |
| 🔴 Critical | 16     | 40%      |
| 🟡 Normal   | 21     | 52.5%    |
| **Total**   | **40** | **100%** |

### 7.3 Estabilidade (Flaky Rate)

| Metrica                          | Valor         |
| -------------------------------- | ------------- |
| Execucoes consecutivas sem falha | > 10          |
| Flaky rate                       | 0%            |
| Ambiente consistente             | Local + CI/CD |

---

## 🔄 8. Execucao CI/CD

### 8.1 Pipeline

| Workflow | Trigger           | Schedule      | Jobs                                      |
| -------- | ----------------- | ------------- | ----------------------------------------- |
| `ci.yml` | Push/PR para main | 08h e 16h BRT | deploy-monitoring → api-tests + web-tests |

### 8.2 Artefatos Gerados

| Artefato            | Destino                                    | URL                                              |
| ------------------- | ------------------------------------------ | ------------------------------------------------ |
| Allure Report (Web) | `/srv/apps/agibank-challenge-reports/web/` | https://rennangimenez.com/agibank-challenge/web/ |
| Allure Report (API) | `/srv/apps/agibank-challenge-reports/api/` | https://rennangimenez.com/agibank-challenge/api/ |
| Metricas InfluxDB   | `test_results`, `test_stability`           | Grafana Quality Overview                         |

### 8.3 Metricas de Pipeline

| Metrica                      | Valor                             |
| ---------------------------- | --------------------------------- |
| ⏱️ Duracao media (API tests) | ~60-90s                           |
| ⏱️ Duracao media (Web tests) | ~120-180s                         |
| 📊 Feedback time             | < 5 min total                     |
| 📈 Historico                 | Grafana Pipeline Health dashboard |

---

## 📎 9. Evidencias e Links

| Evidencia                     | Tipo             | URL                                                                      |
| ----------------------------- | ---------------- | ------------------------------------------------------------------------ |
| 🌐 Allure Report — Web        | HTML interativo  | https://rennangimenez.com/agibank-challenge/web/                         |
| 🔗 Allure Report — API        | HTML interativo  | https://rennangimenez.com/agibank-challenge/api/                         |
| 👁️ Grafana — Quality Overview | Dashboard        | https://rennangimenez.com/grafana/                                       |
| 👁️ Grafana — Pipeline Health  | Dashboard        | https://rennangimenez.com/grafana/                                       |
| 🔄 GitHub Actions             | Logs de execucao | https://github.com/rennangimenez/agibank-qa-automation-challenge/actions |

---

## 📌 10. Conclusao

A suite de testes Web + API apresenta **100% de pass rate** com **40 cenarios automatizados** cobrindo funcionalidades, contratos, edge cases e seguranca. Os principais destaques:

| Destaque                                | Status                           |
| --------------------------------------- | -------------------------------- |
| ✅ 40/40 testes passando                | Estabilidade total               |
| ⚠️ 2 findings de seguranca documentados | HTML Injection + Info Disclosure |
| ✅ Padrao AAA implementado com fixtures | Separacao de responsabilidades   |
| ✅ CI/CD com execucao programada        | 2x/dia (08h e 16h BRT)           |
| ✅ Metricas em tempo real               | Grafana + InfluxDB               |
| ✅ 0% flaky rate                        | Estabilidade comprovada          |

A execucao automatizada garante monitoramento continuo da qualidade, com visibilidade completa via dashboards Grafana e reports Allure online.
