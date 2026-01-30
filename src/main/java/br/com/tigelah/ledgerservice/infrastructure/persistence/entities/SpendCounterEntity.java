package br.com.tigelah.ledgerservice.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "spend_counter",
        indexes = @Index(name="idx_spend_counter_lookup",
                columnList="scope_type,scope_key,period_type,period_start"))
@IdClass(SpendCounterId.class)
public class SpendCounterEntity {
    @Id @Column(name="scope_type", nullable=false) public String scopeType;
    @Id @Column(name="scope_key", nullable=false) public String scopeKey;
    @Id @Column(name="period_type", nullable=false) public String periodType;
    @Id @Column(name="period_start", nullable=false) public Instant periodStart;

    @Column(nullable=false, length=3) public String currency;
    @Column(name="amount_cents", nullable=false) public long amountCents;
    @Column(name="updated_at", nullable=false) public Instant updatedAt;
}
