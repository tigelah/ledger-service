package br.com.tigelah.ledgerservice.application.usecase;

import java.util.UUID;

public record AvailableCredit(UUID accountId, long availableCents, String currency, long holdsCents, long capturedCents) {}
