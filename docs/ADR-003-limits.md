# ADR-003 — Limites por usuário/cartão + diário/mensal + consumo na captura

## Contexto
Simular crédito real: reserva (autorização) e consumo (captura), com limites configuráveis e controle de gasto por janela.

## Decisão
Persistir limites no ledger e contabilizar gasto por janela somente após CAPTURE.

## Consequências
- Positivas: auditável, consistente, evita consumo fantasma.
- Negativas/Trade-offs: requer read model (spend_counter).

## Alternativas consideradas
Consumir na autorização (rejeitado).

## Observações de implementação
scope PAN deve ser hash/token.
