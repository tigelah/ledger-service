package br.com.tigelah.ledgerservice.domain.model;

import java.time.Instant;

public record SpendCounter(LimitScopeType scopeType, String scopeKey, PeriodType periodType, Instant periodStart,
                           String currency, long amountCents, Instant updatedAt) {}

