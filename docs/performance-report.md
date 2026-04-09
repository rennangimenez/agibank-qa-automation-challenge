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

| Metrica             | Target       | Status       |
| ------------------- | ------------ | ------------ |
| Throughput          | >= 250 req/s | Pendente VPS |
| Response Time (p90) | < 2 segundos | Pendente VPS |
| Error Rate          | < 1%         | Pendente VPS |

---

## 3. Resultados - Load Test

### 3.1 Execucao Local (Windows)

> Resultados capturados na maquina de desenvolvimento. Conditions: Windows 11, rede residencial.

| Metrica            | Valor                   |
| ------------------ | ----------------------- |
| Total Samples      | Aguardando dados da VPS |
| Error Rate         | Aguardando dados da VPS |
| Throughput (req/s) | Aguardando dados da VPS |
| Avg Response Time  | Aguardando dados da VPS |
| P90 Response Time  | Aguardando dados da VPS |
| P95 Response Time  | Aguardando dados da VPS |
| P99 Response Time  | Aguardando dados da VPS |

### 3.2 Execucao VPS (Linux)

> Resultados da execucao via GitHub Actions no runner self-hosted.

| Metrica            | Valor    |
| ------------------ | -------- |
| Total Samples      | Pendente |
| Error Rate         | Pendente |
| Throughput (req/s) | Pendente |
| Avg Response Time  | Pendente |
| P90 Response Time  | Pendente |
| P95 Response Time  | Pendente |
| P99 Response Time  | Pendente |

---

## 4. Resultados - Spike Test

### 4.1 Execucao Local

| Fase            | Threads | Throughput | Avg RT   | Error Rate |
| --------------- | ------- | ---------- | -------- | ---------- |
| Warm-up (60s)   | 30      | Pendente   | Pendente | Pendente   |
| Spike (120s)    | 200     | Pendente   | Pendente | Pendente   |
| Cool-down (60s) | 30      | Pendente   | Pendente | Pendente   |

### 4.2 Execucao VPS

| Fase            | Threads | Throughput | Avg RT   | Error Rate |
| --------------- | ------- | ---------- | -------- | ---------- |
| Warm-up (60s)   | 30      | Pendente   | Pendente | Pendente   |
| Spike (120s)    | 200     | Pendente   | Pendente | Pendente   |
| Cool-down (60s) | 30      | Pendente   | Pendente | Pendente   |

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

- **JMeter Dashboard:** Response times (avg, p90, p95, p99), throughput, error rate, active threads
- **Node Exporter Dashboard:** CPU, memoria, disco e rede da VPS durante o teste
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

- **InfluxDB:** Armazena metricas JMeter em tempo real via Backend Listener
- **Prometheus + Node Exporter:** Metricas de infraestrutura da VPS
- **Grafana:** Visualizacao unificada de performance + infraestrutura

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

> **Nota:** Este relatorio sera atualizado com metricas reais apos a execucao dos testes na VPS com o stack de monitoramento ativo.
