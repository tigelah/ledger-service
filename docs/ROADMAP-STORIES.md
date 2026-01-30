# Roadmap — Limites (PAG-02)

## Story: Configurar limite por usuário/cartão
- API no ledger para PUT/GET por userId ou panHash
- Persistência: limit_rule

## Story: Implementar limite diário e mensal
- Projeção spend_counter (DAY/MONTH)
- Reset automático via period_start

## Story: Consumir limite somente após captura
- payment.authorized => HOLD
- payment.captured => CAPTURE + RELEASE_HOLD + atualiza spend_counter
