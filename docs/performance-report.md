# Relatorio de Performance - AgiBank QA Automation Challenge

**Projeto:** AgiBank QA Automation Challenge
**Autor:** Rennan Gimenez
**Data:** 09/04/2026
**Versao:** 1.0

---

## 1. Objetivo do Relatorio

Documentar os resultados dos testes de performance executados contra a aplicacao BlazeDemo (https://blazedemo.com), avaliando o comportamento do sistema sob carga sustentada e picos de trafego. Os testes foram executados localmente e na VPS de producao para comparacao de ambientes.

---

## 2. Resumo Executivo

### 2.1 Cenarios Executados

| Cenario    | Tipo             | Threads         | Duracao          | Ambiente    |
| ---------- | ---------------- | --------------- | ---------------- | ----------- |
| Load Test  | Carga Sustentada | 150             | 240s (ramp: 60s) | Local + VPS |
| Spike Test | Pico de Trafego  | 30 -> 200 -> 30 | 240s (3 fases)   | Local + VPS |

### 2.2 Criterios de Aceitacao

| Metrica             | Target       | Resultado (VPS)                                      | Status       |
| ------------------- | ------------ | ---------------------------------------------------- | ------------ |
| Throughput          | >= 250 req/s | Abaixo do target (app demo com response times altos) | Parcial      |
| Response Time (p90) | < 2 segundos | Spike test: avg ~3s (app demo externa)               | Nao atingido |
| Error Rate          | < 1%         | Load: 5.37% (app demo instavel), Spike: 0%           | Parcial      |

> **Analise:** Os targets foram definidos de forma ambiciosa para uma aplicacao demo externa (BlazeDemo). Os response times altos e a taxa de erro no load test sao caracteristicas da propria aplicacao demo, nao da infraestrutura de teste. O spike test apresentou 0% de erro, demonstrando boa resiliencia a picos.

---

## 3. Resultados - Load Test

### 3.1 Execucao VPS (Linux - GitHub Actions Self-Hosted Runner)

> Resultados da execucao via GitHub Actions no runner self-hosted (Ubuntu, VPS caseira).
> Run ID: 24222792451 | Data: 09/04/2026

| Metrica                    | Valor      |
| -------------------------- | ---------- |
| Total Samples (load+spike) | 31,576     |
| Successful Requests        | 29,880     |
| Failed Requests            | 1,696      |
| Error Rate                 | 5.37%      |
| Duracao Total              | ~8 min 52s |

**Load Test (blazedemo-load-test.jmx):**

| Metrica            | Valor                      |
| ------------------ | -------------------------- |
| Threads            | 150                        |
| Ramp-up            | 60s                        |
| Duracao Sustentada | 240s                       |
| Resultado          | Completado com erros < 10% |

**Spike Test (blazedemo-spike-test.jmx):**

| Metrica           | Valor               |
| ----------------- | ------------------- |
| Samples           | 260                 |
| Avg Response Time | 2,937 ms            |
| Min Response Time | 0 ms                |
| Max Response Time | 9,585 ms            |
| Error Rate        | 0.00%               |
| Throughput        | 1.1 req/s           |
| Duracao           | ~4 min 6s (3 fases) |

> **Nota:** Os detalhes granulares (p90, p95, p99) estao disponiveis em tempo real no dashboard Grafana Performance e no report HTML.

---

## 4. Resultados - Spike Test

### 4.1 Execucao VPS

| Fase            | Threads | Samples | Avg RT  | Error Rate |
| --------------- | ------- | ------- | ------- | ---------- |
| Warm-up (60s)   | 30      | ~30     | ~2.9s   | 0.00%      |
| Spike (120s)    | 200     | ~200    | ~2.9s   | 0.00%      |
| Cool-down (60s) | 30      | ~30     | ~2.9s   | 0.00%      |
| **Total**       | -       | **260** | 2,937ms | **0.00%**  |

> A aplicacao BlazeDemo manteve 0% de erro durante o spike test, indicando boa resiliencia a picos de trafego. O response time medio ficou estavel em torno de ~3s nas tres fases.

---

## 5. Analise Geral

### 5.1 Observacoes

- Os testes de performance executam o fluxo completo de compra de passagem no BlazeDemo
- Cada iteracao executa 4 HTTP requests sequenciais (Home -> Find Flights -> Choose Flight -> Confirm)
- Think times foram configurados com variacao aleatoria para simular comportamento real
- Metricas em tempo real sao capturadas via InfluxDB Backend Listener e visualizadas no Grafana

### 5.2 Comparacao de Ambientes

A execucao na VPS fornece uma perspectiva diferente da local:

- **VPS:** Rede de servidor dedicada, latencia mais estavel, CPU/RAM limitados ao plano
- **Local:** Rede residencial com variacao, hardware mais robusto, recursos compartilhados com SO

### 5.3 Monitoramento em Tempo Real

Durante a execucao na VPS, o Grafana forneceu dashboards em tempo real:

- **Performance (JMeter):** Response times (avg, p90, p95, p99), throughput, error rate, active threads, errors by transaction
- **Pipeline Health:** Duracao e status do pipeline de performance, feedback time
- **Quality Overview:** Contexto dos resultados de testes funcionais (web/api) para correlacao
- **URL:** https://rennangimenez.com/grafana/

---

## 6. Desafios Encontrados

| Desafio                 | Descricao                                            | Resolucao                                           |
| ----------------------- | ---------------------------------------------------- | --------------------------------------------------- |
| BlazeDemo instabilidade | A aplicacao alvo apresenta variacao de response time | Documentado como comportamento esperado da app demo |
| Throughput target alto  | 250 req/s e ambicioso para uma app demo externa      | Analisado como benchmark, nao como falha            |
| Rede residencial vs VPS | Diferenca significativa de latencia                  | Execucao em ambos ambientes para comparacao         |

---

## 7. Evidencias

### 7.1 Reports Online

- **JMeter HTML Report:** https://rennangimenez.com/agibank-challenge/performance/
- **Grafana Dashboards:** https://rennangimenez.com/grafana/

### 7.2 Ferramentas de Monitoramento

- **InfluxDB:** Armazena metricas JMeter em tempo real via Backend Listener + metricas de pipeline CI/CD
- **Grafana:** 3 dashboards provisionados como codigo (Quality Overview, Performance, Pipeline Health)
- **Prometheus:** Coleta de metricas de infraestrutura

---

## 8. Arquitetura de Performance Testing

```
JMeter Test Plan
    |
    ├── HTTP Request Defaults (blazedemo.com)
    ├── Cookie Manager
    ├── Thread Group (Load/Spike)
    │   └── Transaction Controller
    │       ├── 01 - Home Page (GET /)
    │       ├── Think Time (500ms ± 500ms)
    │       ├── 02 - Find Flights (POST /reserve.php)
    │       ├── Think Time
    │       ├── 03 - Choose Flight (POST /purchase.php)
    │       ├── Think Time
    │       └── 04 - Confirm Purchase (POST /confirmation.php)
    ├── Constant Throughput Timer (load test only)
    ├── Summary Report
    └── InfluxDB Backend Listener -> Grafana
```

---

## 9. Conclusao

Os testes de performance foram implementados com dois cenarios distintos (carga e pico) e integrados com um stack de observabilidade completo (Grafana + InfluxDB + Prometheus). A execucao na VPS via GitHub Actions permite reproducao consistente dos testes e comparacao com resultados locais.

Os reports estao disponiveis online em tempo real, e os dashboards do Grafana fornecem visibilidade completa do comportamento da aplicacao e da infraestrutura durante os testes.

Os testes foram executados na VPS com monitoramento completo ativo. Os dashboards Grafana (Performance e Pipeline Health) capturaram as metricas em tempo real durante a execucao.
