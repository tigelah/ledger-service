package br.com.tigelah.ledgerservice.domain.ports;

import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import br.com.tigelah.ledgerservice.domain.model.PeriodType;
import br.com.tigelah.ledgerservice.domain.model.SpendCounter;

import java.time.Instant;
import java.util.Optional;

public interface SpendCounterRepository {
    Optional<SpendCounter> find(LimitScopeType scopeType, String scopeKey, PeriodType periodType, Instant periodStart);
    SpendCounter save(SpendCounter counter);
}
