# CONTEXTO DO PROJETO --- TESTE TÉCNICO QA SÊNIOR (AGIBANK)

## Objetivo

Desenvolver uma solução completa de automação de testes cobrindo: -
Web - API - Performance

Foco em demonstrar senioridade técnica, arquitetura e boas práticas.

## Decisão Arquitetural

### Stack principal

- Linguagem: Java 17
- Build: Maven
- Test Runner: JUnit 5

### Web

- Playwright Java
- Padrão Page Object (leve)
- Allure Reports

### API

- Rest Assured
- Estrutura baseada em client layer
- Validação de contrato e dados
- Allure Reports

### Performance

- JMeter
- Testes de carga e pico
- HTML Report

### CI/CD

- GitHub Actions

## Estrutura do Projeto (Monorepo)

    agi-qa-technical-challenge/
      README.md
      pom.xml
      .github/workflows/

      web-tests/
      api-tests/
      performance-tests/

## Diretrizes Técnicas

### Web

- Evitar flakiness
- Uso de auto-waits do Playwright
- Separação clara entre Page, Component e Test

### API

- Criar camada de client (DogApiClient)
- Não misturar lógica HTTP com testes
- Validar status, payload e cenários negativos

### Performance

- Cenário: compra de passagem
- Meta: 250 req/s e p90 \< 2s
- Explicar resultado no README

## Boas Práticas Esperadas

- Clean Code
- Legibilidade
- Baixo acoplamento
- README detalhado
- Execução simples

## Estratégia de Avaliação (implícita)

O avaliador observará: - Organização do código - Escolhas técnicas -
Clareza da documentação - Capacidade de justificar decisões

## Posicionamento Técnico

Esta arquitetura foi escolhida para: - Alinhar com requisito de Java
para vaga sênior - Demonstrar conhecimento moderno (Playwright) -
Garantir confiabilidade e manutenção - Facilitar execução pelo avaliador

## Instrução para o Cursor

Gerar: 1. Estrutura completa do projeto Maven multi-module 2.
Dependências necessárias 3. Boilerplate inicial para: - Web
(Playwright) - API (RestAssured) 4. README base 5. Pipeline GitHub
Actions

Data de geração: 2026-04-09 21:18:56.578522
