
# ADR-001 — Ledger como Fonte de Verdade Financeira

## Contexto
O simulador de adquirência evoluiu para um modelo event-driven com múltiplos serviços (orquestração, risco, emissor, clearing e settlement).
O uso de status mutável em tabelas de pagamento não atende requisitos reais de:
- auditoria
- reconciliação
- chargeback
- replay de eventos

Sistemas financeiros reais utilizam **ledgers imutáveis** como fonte de verdade.

## Decisão
Criar um serviço dedicado (**ledger-service**) responsável por registrar lançamentos financeiros imutáveis (append-only),
derivados exclusivamente de eventos de domínio.

O Ledger:
- Não é atualizado
- Não apaga dados
- Apenas registra lançamentos (débitos/créditos)

## Consequências Positivas
- Auditabilidade completa
- Reconciliação determinística
- Suporte natural a chargebacks e estornos
- Replay de eventos Kafka sem inconsistência
- Separação clara entre domínio financeiro e operacional

## Consequências Negativas / Trade-offs
- Maior complexidade inicial
- Necessidade de read models (CQRS)
- Curva de aprendizado maior para o time

## Alternativas Consideradas
1. Atualizar saldo diretamente em tabela (rejeitada — não auditável)
2. Ledger embutido no acquirer-core (rejeitada — acoplamento alto)
3. Event Sourcing puro (avaliado — excesso de complexidade neste estágio)

## Observações de Implementação
- Ledger é write-only
- Consultas usam projections
- Integração via Kafka + Outbox
- Crédito disponível = limite - holds
