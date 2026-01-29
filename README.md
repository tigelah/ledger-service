# ledger-service

Ledger Service é a **fonte de verdade contábil** do simulador. Ele mantém um **livro razão (ledger) imutável** (append-only) e expõe uma API mínima para consulta de **crédito disponível**.

## O que este serviço faz
- Mantém `ledger_account` (limite de crédito por conta)
- Mantém `ledger_entry` (lançamentos imutáveis)
- Consome `payment.authorized` e cria um lançamento `HOLD` (débito)
- Publica `ledger.entry.recorded` via **Outbox Pattern**
- Expõe endpoint para o emissor consultar crédito disponível (`/available-credit`)

## Endpoints
### Criar conta
`POST /accounts`

Body:
```json
{ "accountId": "uuid", "creditLimitCents": 1000, "currency": "BRL" }
```

### Consultar crédito disponível
`GET /accounts/{accountId}/available-credit`

Respostas:
- `200 OK` `{ accountId, availableCents, currency }`
- `404 Not Found` se conta não existir

## Eventos Kafka
### Consumidos
- `payment.authorized`  
  Campos mínimos: `eventId`, `occurredAt`, `correlationId`, `paymentId`, `accountId`, `amountCents`, `currency`

### Produzidos (via Outbox)
- `ledger.entry.recorded`

## Outbox Pattern
1) Consumer grava `ledger_entry`
2) Enfileira `outbox_event` na mesma transação
3) Worker publica no Kafka e marca como `SENT`

## Como rodar
### Variáveis de ambiente
Veja `.env.example`.

### Rodar local (Maven)
```bash
mvn clean spring-boot:run
```

### Rodar com Docker
Assume rede `acquiring-net` + Postgres/Kafka já rodando.
```bash
docker compose up -d --build
```

## Health e métricas
- `GET /actuator/health`
- `GET /actuator/prometheus`

## Testes e cobertura
```bash
mvn clean verify
```

Relatório:
- `target/site/jacoco/index.html`
