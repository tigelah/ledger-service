package br.com.tigelah.ledgerservice.application.handlers;

import br.com.tigelah.ledgerservice.application.usecase.RecordHoldFromAuthorizationUseCase;
import br.com.tigelah.ledgerservice.domain.ports.EventPublisher;
import br.com.tigelah.ledgerservice.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class AuthorizationEventHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationEventHandler.class);
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
        recordHold.execute(accountId, paymentId, amountCents, currency, correlationId);


        try {
            var payload = new HashMap<String, Object>();
            payload.put("eventId", eventId.toString());
            payload.put("occurredAt", occurredAt);
            payload.put("correlationId", correlationId);
            payload.put("paymentId", paymentId.toString());
            payload.put("accountId", accountId.toString());
            payload.put("amountCents", amountCents);
            payload.put("currency", currency);

            events.publish(
                    Topics.LEDGER_ENTRY_RECORDED,
                    paymentId.toString(),
                    mapper.writeValueAsString(payload)
            );
        } catch (Exception e) {
            log.error("failed_to_publish_ledger_entry_recorded message={}", e);
            throw new RuntimeException("failed_to_publish_ledger_entry_recorded", e);
        }
    }
}