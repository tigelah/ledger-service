package br.com.tigelah.ledgerservice.entrypoints.http.dto;

import br.com.tigelah.ledgerservice.domain.model.LimitRule;

public record LimitRuleResponse(String scopeType, String scopeKey, String currency, long creditLimitCents, long dailyLimitCents, long monthlyLimitCents) {
    public static LimitRuleResponse from(LimitRule r) {
        return new LimitRuleResponse(r.scopeType().name(), r.scopeKey(), r.currency(), r.creditLimitCents(), r.dailyLimitCents(), r.monthlyLimitCents());
    }
}
