package br.com.tigelah.ledgerservice.application.handlers;

import br.com.tigelah.ledgerservice.application.usecase.RecordHoldFromAuthorizationUseCase;
import br.com.tigelah.ledgerservice.domain.ports.EventPublisher;
import br.com.tigelah.ledgerservice.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.UUID;

public class AuthorizationEventHandler {
    private final RecordHoldFromAuthorizationUseCase recordHold;
    private final EventPublisher events;
    private final ObjectMapper mapper;

    public AuthorizationEventHandler(RecordHoldFromAuthorizationUseCase recordHold, EventPublisher events, ObjectMapper mapper) {
        this.recordHold = recordHold;
        this.events = events;
        this.mapper = mapper;
    }

    public void onPaymentAuthorized(UUID eventId, String occurredAt, String correlationId,
                                    UUID paymentId, UUID accountId, long amountCents, String currency) {
        var entry = recordHold.execute(accountId, paymentId, amountCents, currency, correlationId);
        return;

    }
}