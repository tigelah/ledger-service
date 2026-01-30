package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.LimitRule;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import br.com.tigelah.ledgerservice.domain.ports.LimitRuleRepository;

import java.util.Optional;

public class GetLimitRuleUseCase {
    private final LimitRuleRepository repo;

    public GetLimitRuleUseCase(LimitRuleRepository repo) {
        this.repo = repo;
    }

    public Optional<LimitRule> execute(LimitScopeType type, String key) {
        if (type == null) throw new IllegalArgumentException("scope_type_required");
        if (key == null || key.isBlank()) throw new IllegalArgumentException("scope_key_required");
        return repo.findByScope(type, key);
    }
}
