package br.com.tigelah.ledgerservice.entrypoints.kafka;

import br.com.tigelah.ledgerservice.application.handlers.AuthorizationEventHandler;
import br.com.tigelah.ledgerservice.application.handlers.CaptureEventHandler;
import br.com.tigelah.ledgerservice.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class LedgerEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(LedgerEventsConsumer.class);

    private final ObjectMapper mapper;
    private final AuthorizationEventHandler authorizationHandler;
    private final CaptureEventHandler captureHandler;

    public LedgerEventsConsumer(ObjectMapper mapper,
                                AuthorizationEventHandler authorizationHandler,
                                CaptureEventHandler captureHandler) {
        this.mapper = mapper;
        this.authorizationHandler = authorizationHandler;
        this.captureHandler = captureHandler;
    }

    @KafkaListener(
            topics = { Topics.PAYMENT_AUTHORIZED, Topics.PAYMENT_CAPTURED },
            groupId = "${spring.kafka.consumer.group-id:ledger-service}"
    )
    @Transactional
    public void onMessage(String message) {
        try {
            JsonNode root = mapper.readTree(message);

            var type = root.path("type").asText("");
            var eventId = UUID.fromString(root.path("eventId").asText(UUID.randomUUID().toString()));
            var occurredAt = parseInstant(root.path("occurredAt").asText(null));
            var correlationId = root.path("correlationId").asText("n/a");

            var paymentId = UUID.fromString(root.path("paymentId").asText());
            var accountId = UUID.fromString(root.path("accountId").asText());
            var amountCents = root.path("amountCents").asLong();
            var currency = root.path("currency").asText("BRL");

            if (Topics.PAYMENT_AUTHORIZED.equals(type)) {
                authorizationHandler.onPaymentAuthorized(
                        eventId, occurredAt.toString(), correlationId,
                        paymentId, accountId, amountCents, currency
                );
                return;
            }

            if (Topics.PAYMENT_CAPTURED.equals(type)) {
                long interestCents = root.path("interestCents").asLong(0);
                captureHandler.onPaymentCaptured(
                        eventId, occurredAt, correlationId,
                        paymentId, accountId, amountCents, interestCents, currency
                );
                return;
            }

            log.warn("ledger_unknown_event type={} payload={}", type, root);

        } catch (Exception e) {
            log.error("failed_to_consume message={}", message, e);
        }
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) return Instant.now();
        return Instant.parse(value);
    }
}