package br.com.tigelah.ledgerservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record LimitRule(UUID id, LimitScopeType scopeType, String scopeKey, String currency,
                        long creditLimitCents, long dailyLimitCents, long monthlyLimitCents,
                        Instant createdAt, Instant updatedAt) {}

