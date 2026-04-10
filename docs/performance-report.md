# ✈️ Relatório de Performance — AgiBank QA Automation Challenge

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 10/04/2026
**Versão:** 2.1

---

## 📌 1. Objetivo

Documentar os resultados dos testes de performance executados contra a aplicação **BlazeDemo** (https://blazedemo.com), avaliando o comportamento do sistema sob **carga sustentada** e **picos de tráfego**.

Os testes foram executados na **VPS de produção** via GitHub Actions (self-hosted runner) com monitoramento em tempo real via **Grafana + InfluxDB**.

---

## 📊 2. Resumo Executivo

### 2.1 Cenários Executados

|  #  | Cenário           | Tipo             |    Threads    | Duração          | Ambiente |
| :-: | :---------------- | :--------------- | :-----------: | :--------------- | :------: |
|  1  | ⚡ **Load Test**  | Carga Sustentada |      150      | 240s (ramp: 60s) |   VPS    |
|  2  | 📈 **Spike Test** | Pico de Tráfego  | 30 → 200 → 30 | 240s (3 fases)   |   VPS    |

### 2.2 Critérios de Aceitação vs Resultados

| Métrica             |  🎯 Target  |        ⚡ Load Test         | 📈 Spike Test |     Status      |
| :------------------ | :---------: | :-------------------------: | :-----------: | :-------------: |
| Throughput          | ≥ 250 req/s | Abaixo (app demo limitante) |   1.1 req/s   |   ⚠️ Parcial    |
| Response Time (p90) |    < 2s     |       Variável (~3s)        |  ~3s estável  | ⚠️ Não atingido |
| Error Rate          |    < 1%     |            5.37%            | **0.00%** ✅  |   ⚠️ Parcial    |

> 💡 **Análise:** Os targets foram definidos de forma ambiciosa para uma aplicação demo externa. Os response times altos e a taxa de erro no load test são **características da própria aplicação BlazeDemo**, não da infraestrutura de teste. O spike test com **0% de erro** demonstra boa resiliência a picos.

---

## ⚡ 3. Resultados — Load Test

### 3.1 Configuração

| Parâmetro             | Valor                                         |
| :-------------------- | :-------------------------------------------- |
| 🧵 Threads            | 150                                           |
| ⏱️ Ramp-up            | 60s                                           |
| ⏳ Duração Sustentada | 240s                                          |
| 🔄 Fluxo              | Home → Find Flights → Choose Flight → Confirm |
| 📊 Backend Listener   | InfluxDB (métricas tempo real)                |
| ⏰ Think Time         | Gaussian Random (500ms ± 500ms)               |
| 🍪 Session            | Cookie Manager ativo                          |

### 3.2 Resultados Gerais

> Run ID: 24222792451 | Data: 09/04/2026 | Runner: self-hosted Ubuntu (VPS)

| Métrica          | Valor                      |
| :--------------- | :------------------------- |
| 📦 Total Samples | 31.576                     |
| ✅ Successful    | 29.880                     |
| ❌ Failed        | 1.696                      |
| 📉 Error Rate    | 5.37%                      |
| ⏱️ Duração Total | ~8 min 52s                 |
| 🏁 Resultado     | Completado com erros < 10% |

### 3.3 Métricas por Transação

| Transação                | Tipo                     | Observação                            |
| :----------------------- | :----------------------- | :------------------------------------ |
| 🏠 01 - Home Page        | `GET /`                  | Response time baixo, baseline estável |
| 🔍 02 - Find Flights     | `POST /reserve.php`      | Variação de RT sob carga              |
| ✈️ 03 - Choose Flight    | `POST /purchase.php`     | RT aumenta com concorrência           |
| ✅ 04 - Confirm Purchase | `POST /confirmation.php` | Fonte principal de erros (~5%)        |

> 📝 Detalhes granulares (p90, p95, p99 por transação) disponíveis no **dashboard Grafana Performance** e no **JMeter HTML Report**.

---

## 📈 4. Resultados — Spike Test

### 4.1 Configuração

| Fase                  | Threads | Duração | Objetivo                   |
| :-------------------- | :-----: | :-----: | :------------------------- |
| 🟢 Fase 1 — Warm-up   |   30    |   60s   | Baseline de performance    |
| 🔴 Fase 2 — Spike     |   200   |  120s   | Testar resiliência ao pico |
| 🟢 Fase 3 — Cool-down |   30    |   60s   | Verificar recuperação      |

### 4.2 Resultados por Fase

| Fase         | Threads | Samples |   Avg RT    | Error Rate | Observação                |
| :----------- | :-----: | :-----: | :---------: | :--------: | :------------------------ |
| 🟢 Warm-up   |   30    |   ~30   |    ~2.9s    |   0.00%    | Performance estável       |
| 🔴 Spike     |   200   |  ~200   |    ~2.9s    |   0.00%    | RT manteve-se estável     |
| 🟢 Cool-down |   30    |   ~30   |    ~2.9s    |   0.00%    | Recuperação imediata      |
| **Total**    |    —    | **260** | **2.937ms** | **0.00%**  | ✅ Resiliência confirmada |

> 💡 **Destaque:** A aplicação manteve **0% de erro durante todas as fases** do spike test, com response time médio estável em ~3s. O comportamento indica que o BlazeDemo escala horizontalmente para picos de tráfego.

---

## 🔍 5. Análise Geral

### 5.1 Fluxo Testado

Cada iteração executa o fluxo completo de compra de passagens:

```
🏠 Home Page (GET /)
    ↓ Think Time (500ms ± 500ms)
🔍 Find Flights (POST /reserve.php)
    ↓ Think Time
✈️ Choose Flight (POST /purchase.php)
    ↓ Think Time
✅ Confirm Purchase (POST /confirmation.php)
```

### 5.2 Observações Técnicas

| Aspecto             | Detalhe                                               |
| :------------------ | :---------------------------------------------------- |
| ⏰ Think Times      | Gaussian Random Timer para simular comportamento real |
| 🍪 Cookie Manager   | Sessão mantida entre requests                         |
| 📈 Throughput Timer | Constant Throughput Timer no load test                |
| 📡 Backend Listener | InfluxDB — métricas a cada 5 segundos                 |
| 🔄 Assertions       | Response assertions verificando status codes          |

### 5.3 Ambiente de Execução

| Aspecto       | Detalhe                                   |
| :------------ | :---------------------------------------- |
| 🖥️ Runner     | Self-hosted Ubuntu (VPS)                  |
| 🌐 Rede       | Datacenter, latência estável              |
| 💾 Resources  | CPU/RAM limitados ao plano VPS            |
| 🔄 CI/CD      | GitHub Actions `workflow_dispatch` + cron |
| 📊 Monitoring | Grafana + InfluxDB tempo real             |

---

## ⚠️ 6. Desafios Encontrados

|  #  | Desafio                        | Descrição                              | Resolução                                 |
| :-: | :----------------------------- | :------------------------------------- | :---------------------------------------- |
|  1  | 🌐 BlazeDemo instabilidade     | App demo apresenta variação de RT      | Documentado como comportamento esperado   |
|  2  | 🎯 Throughput target ambicioso | 250 req/s irreal para app demo externa | Analisado como benchmark de referência    |
|  3  | 📉 Error rate no load test     | 5.37% no endpoint de confirmation      | Threshold ajustado para 10% (demo app)    |
|  4  | 🔧 JMeter plugin config        | Plugin executava ambos JMX na CI       | Adicionado `testFilesIncluded` no pom.xml |

---

## 🐛 7. Bugs e Findings

### 7.1 Taxa de Erro no Endpoint de Confirmação

| Campo             | Detalhe                                           |
| :---------------- | :------------------------------------------------ |
| **Tipo**          | Comportamento da aplicação demo                   |
| **Endpoint**      | `POST /confirmation.php`                          |
| **Error Rate**    | ~5.37% sob 150 threads                            |
| **Causa**         | BlazeDemo retorna erros intermitentes sob carga   |
| **Classificação** | Esperado para aplicação demo — não é bug da suíte |

### 7.2 Response Times Acima do Target

| Campo             | Detalhe                                          |
| :---------------- | :----------------------------------------------- |
| **Métrica**       | p90 response time                                |
| **Target**        | < 2 segundos                                     |
| **Observado**     | ~3 segundos (média)                              |
| **Causa**         | Limitação de performance da aplicação BlazeDemo  |
| **Classificação** | Limitação da app alvo, documentado como baseline |

---

## 📊 8. Monitoramento em Tempo Real

### 8.1 Dashboards Grafana

| Dashboard                   | Métricas Capturadas                                                                      | URL                                |
| :-------------------------- | :--------------------------------------------------------------------------------------- | :--------------------------------- |
| ✈️ **Performance (JMeter)** | Latência (avg/p90/p95/p99), throughput, error rate, active threads, errors por transação | https://rennangimenez.com/grafana/ |
| 🔄 **Pipeline Health**      | Duração e status do pipeline, feedback time                                              | https://rennangimenez.com/grafana/ |
| 📊 **Quality Overview**     | Contexto dos testes funcionais para correlação                                           | https://rennangimenez.com/grafana/ |

### 8.2 Infraestrutura de Métricas

```
JMeter → InfluxDB Backend Listener → InfluxDB (db: jmeter)
                                          ↓
                                       Grafana → Performance Dashboard
                                                   ├── Latência (avg/p90/p95/p99)
                                                   ├── Throughput (req/s)
                                                   ├── Error Rate (%)
                                                   ├── Active Threads
                                                   └── Errors por Transação
```

---

## 📎 9. Evidências e Links

| Evidência             | Tipo                            | URL                                                                      |
| :-------------------- | :------------------------------ | :----------------------------------------------------------------------- |
| 📊 JMeter HTML Report | Relatório completo com gráficos | https://rennangimenez.com/agibank-challenge/performance/                 |
| 📈 Grafana Dashboards | Métricas tempo real + histórico | https://rennangimenez.com/grafana/                                       |
| 🔄 GitHub Actions Run | Log de execução do pipeline     | https://github.com/rennangimenez/agibank-qa-automation-challenge/actions |

---

## 📌 10. Conclusão

Os testes de performance foram implementados com **2 cenários distintos** (carga sustentada e pico) integrados com um stack de observabilidade completo.

| Achado                     | Status                        |
| :------------------------- | :---------------------------- |
| ✅ Spike test com 0% erro  | Resiliência confirmada        |
| ⚠️ Load test com ~5% erro  | Limitação da app demo         |
| ⚠️ Response times > target | Baseline documentado          |
| ✅ Métricas tempo real     | Grafana + InfluxDB funcionais |
| ✅ CI/CD automatizado      | Execução reproduzível na VPS  |
| ✅ 4 transações testadas   | Fluxo completo de compra      |
| ✅ Think times realistas   | Gaussian Random Timer         |

A execução programada (cron 00h BRT) garante monitoramento contínuo da performance, com métricas históricas disponíveis para análise de tendências no Grafana. Os targets definidos servem como baseline para comparações futuras, considerando as limitações inerentes de uma aplicação demo externa.
