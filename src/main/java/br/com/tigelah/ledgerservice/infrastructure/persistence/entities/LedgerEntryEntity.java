package br.com.tigelah.ledgerservice.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entry", indexes = {
        @Index(name = "idx_ledger_entry_account", columnList = "account_id,occurred_at"),
        @Index(name = "idx_ledger_entry_payment", columnList = "payment_id")
})
public class LedgerEntryEntity {
    @Id public UUID id;

    @Column(name = "account_id", nullable = false)
    public UUID accountId;

    @Column(name = "payment_id")
    public UUID paymentId;

    @Column(name = "entry_type", nullable = false)
    public String entryType;

    @Column(name = "direction", nullable = false)
    public String direction;

    @Column(name = "amount_cents", nullable = false)
    public long amountCents;

    @Column(nullable = false)
    public String currency;

    @Column(name = "occurred_at", nullable = false)
    public Instant occurredAt;

    @Column(name = "correlation_id", nullable = false)
    public String correlationId;
}
