package br.com.tigelah.ledgerservice.infrastructure.persistence.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "limit_rule",
        uniqueConstraints = @UniqueConstraint(columnNames = {"scope_type", "scope_key"}))
public class LimitRuleEntity {

    @Id
    public UUID id;

    @Column(name = "scope_type", nullable = false)
    public String scopeType;

    @Column(name = "scope_key", nullable = false)
    public String scopeKey;

    @Column(name = "currency",nullable = false)
    public String currency;

    @Column(name = "credit_limit_cents", nullable = false)
    public long creditLimitCents;

    @Column(name = "daily_limit_cents", nullable = false)
    public long dailyLimitCents;

    @Column(name = "monthly_limit_cents", nullable = false)
    public long monthlyLimitCents;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @Version
    public long version;
}
