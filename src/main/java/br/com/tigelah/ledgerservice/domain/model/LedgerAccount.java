package br.com.tigelah.ledgerservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class LedgerAccount {
    private final UUID id;
    private final long creditLimitCents;
    private final String currency;
    private final Instant createdAt;

    public LedgerAccount(UUID id, long creditLimitCents, String currency, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.creditLimitCents = creditLimitCents;
        this.currency = Objects.requireNonNull(currency);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public UUID id() { return id; }
    public long creditLimitCents() { return creditLimitCents; }
    public String currency() { return currency; }
    public Instant createdAt() { return createdAt; }
}

