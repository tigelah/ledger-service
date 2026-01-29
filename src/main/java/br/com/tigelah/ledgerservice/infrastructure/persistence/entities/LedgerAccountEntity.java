package br.com.tigelah.ledgerservice.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_account")
public class LedgerAccountEntity {
    @Id public UUID id;

    @Column(name = "credit_limit_cents", nullable = false)
    public long creditLimitCents;

    @Column(nullable = false)
    public String currency;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Version
    public long version;
}
