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

    public LedgerEntry(UUID id,
                       UUID accountId,
                       UUID paymentId,
                       EntryType entryType,
                       EntryDirection direction,
                       long amountCents,
                       String currency,
                       Instant occurredAt,
                       String correlationId) {
        this.id = Objects.requireNonNull(id, "id");
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.paymentId = paymentId;
        this.entryType = Objects.requireNonNull(entryType, "entryType");
        this.direction = Objects.requireNonNull(direction, "direction");
        if (amountCents <= 0) throw new IllegalArgumentException("amountCents must be > 0");
        this.amountCents = amountCents;
        this.currency = requireNonBlank(currency, "currency").toUpperCase();
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.correlationId = requireNonBlank(correlationId, "correlationId");
    }


    public static LedgerEntry debit(UUID accountId,
                                    UUID paymentId,
                                    EntryType entryType,
                                    long amountCents,
                                    String currency,
                                    Instant occurredAt,
                                    String correlationId) {
        return new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                paymentId,
                entryType,
                EntryDirection.DEBIT,
                amountCents,
                currency,
                occurredAt,
                correlationId
        );
    }

    public static LedgerEntry credit(UUID accountId,
                                     UUID paymentId,
                                     EntryType entryType,
                                     long amountCents,
                                     String currency,
                                     Instant occurredAt,
                                     String correlationId) {
        return new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                paymentId,
                entryType,
                EntryDirection.CREDIT,
                amountCents,
                currency,
                occurredAt,
                correlationId
        );
    }

    private static String requireNonBlank(String v, String field) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return v;
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