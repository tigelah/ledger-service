package br.com.tigelah.ledgerservice.entrypoints.http.dto;

import java.util.UUID;

public record AvailableCreditResponse(UUID accountId, long availableCents, String currency) {}

