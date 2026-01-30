package br.com.tigelah.ledgerservice.infrastructure.persistence;

import br.com.tigelah.ledgerservice.domain.model.LimitRule;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import br.com.tigelah.ledgerservice.domain.ports.LimitRuleRepository;
import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LimitRuleEntity;
import br.com.tigelah.ledgerservice.infrastructure.persistence.jpa.JpaLimitRuleRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LimitRuleRepositoryAdapter implements LimitRuleRepository {

    private final JpaLimitRuleRepository jpa;

    public LimitRuleRepositoryAdapter(JpaLimitRuleRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public LimitRule upsert(LimitRule rule) {
        var entity = jpa.findByScopeTypeAndScopeKey(
                        rule.scopeType().name(),
                        rule.scopeKey()
                )
                .orElseGet(() -> {
                    var e = new LimitRuleEntity();
                    e.id = rule.id() != null ? rule.id() : UUID.randomUUID();
                    e.createdAt = Instant.now();
                    return e;
                });

        entity.scopeType = rule.scopeType().name();
        entity.scopeKey = rule.scopeKey();
        entity.currency = rule.currency();
        entity.creditLimitCents = rule.creditLimitCents();
        entity.dailyLimitCents = rule.dailyLimitCents();
        entity.monthlyLimitCents = rule.monthlyLimitCents();
        entity.updatedAt = Instant.now();

        var saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<LimitRule> findByScope(String scopeType, String scopeKey) {
        return jpa.findByScopeTypeAndScopeKey(scopeType, scopeKey)
                .map(this::toDomain);
    }

    private LimitRule toDomain(LimitRuleEntity e) {
        return new LimitRule(
                e.id,
                LimitScopeType.valueOf(e.scopeType),
                e.scopeKey,
                e.currency,
                e.creditLimitCents,
                e.dailyLimitCents,
                e.monthlyLimitCents,
                e.createdAt,
                e.updatedAt
        );
    }
}