package br.com.tigelah.ledgerservice.entrypoints.http.dto;

import java.util.UUID;

public record CreateAccountRequest(UUID accountId, long creditLimitCents, String currency) {}
