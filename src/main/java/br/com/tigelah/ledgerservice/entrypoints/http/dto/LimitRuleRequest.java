package br.com.tigelah.ledgerservice.entrypoints.http.dto;

public record LimitRuleRequest(String currency, long creditLimitCents, long dailyLimitCents, long monthlyLimitCents) {}
