package br.com.tigelah.ledgerservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class LedgerEntry {
    private final UUID id;
    private final UUID accountId;
    private final UUID paymentId;
    private final EntryType entryType;
    private final EntryDirection direction;
    private final long amountCents;
    private final String currency;
    private final Instant occurredAt;
    private final String correlationId;

    public LedgerEntry(UUID id, UUID accountId, UUID paymentId, EntryType entryType, EntryDirection direction,
                       long amountCents, String currency, Instant occurredAt, String correlationId) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.paymentId = paymentId;
        this.entryType = Objects.requireNonNull(entryType);
        this.direction = Objects.requireNonNull(direction);
        this.amountCents = amountCents;
        this.currency = Objects.requireNonNull(currency);
        this.occurredAt = Objects.requireNonNull(occurredAt);
        this.correlationId = Objects.requireNonNull(correlationId);
    }

    public UUID id() { return id; }
    public UUID accountId() { return accountId; }
    public UUID paymentId() { return paymentId; }
    public EntryType entryType() { return entryType; }
    public EntryDirection direction() { return direction; }
    public long amountCents() { return amountCents; }
    public String currency() { return currency; }
    public Instant occurredAt() { return occurredAt; }
    public String correlationId() { return correlationId; }
}
