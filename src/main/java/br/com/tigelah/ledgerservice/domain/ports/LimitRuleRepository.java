package br.com.tigelah.ledgerservice.domain.ports;

import br.com.tigelah.ledgerservice.domain.model.LimitRule;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;

import java.util.Optional;

public interface LimitRuleRepository {

    LimitRule upsert(LimitRule rule);

    Optional<LimitRule> findByScope(LimitScopeType  scopeType, String scopeKey);


}
