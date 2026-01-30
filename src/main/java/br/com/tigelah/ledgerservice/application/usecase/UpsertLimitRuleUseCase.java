package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.LimitRule;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import br.com.tigelah.ledgerservice.domain.ports.LimitRuleRepository;

import java.time.Clock;
import java.util.UUID;

public class UpsertLimitRuleUseCase {
    private final LimitRuleRepository repo;
    private final Clock clock;

    public UpsertLimitRuleUseCase(LimitRuleRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    public LimitRule execute(LimitScopeType type, String key, String currency,
                             long credit, long daily, long monthly) {
        if (type == null) throw new IllegalArgumentException("scope_type_required");
        if (key == null || key.isBlank()) throw new IllegalArgumentException("scope_key_required");
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("currency_required");
        if (credit < 0 || daily < 0 || monthly < 0) throw new IllegalArgumentException("limit_must_be_positive");

        var now = clock.instant();
        var rule = new LimitRule(UUID.randomUUID(), type, key, currency.toUpperCase(), credit, daily, monthly, now, now);
        return repo.upsert(rule);
    }
}
