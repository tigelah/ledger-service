package br.com.tigelah.ledgerservice.entrypoints.kafka;

import br.com.tigelah.ledgerservice.application.handlers.AuthorizationEventHandler;
import br.com.tigelah.ledgerservice.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class LedgerEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(LedgerEventsConsumer.class);

    private final ObjectMapper mapper;
    private final AuthorizationEventHandler handler;

    public LedgerEventsConsumer(ObjectMapper mapper, AuthorizationEventHandler handler) {
        this.mapper = mapper;
        this.handler = handler;
    }

    @KafkaListener(topics = { Topics.PAYMENT_AUTHORIZED }, groupId = "${spring.kafka.consumer.group-id:ledger-service}")
    @Transactional
    public void onMessage(String message) {
        try {
            JsonNode root = mapper.readTree(message);

            var eventId = UUID.fromString(root.path("eventId").asText(UUID.randomUUID().toString()));
            var occurredAt = root.path("occurredAt").asText();
            var correlationId = root.path("correlationId").asText("n/a");

            var paymentId = UUID.fromString(root.path("paymentId").asText());
            var accountId = UUID.fromString(root.path("accountId").asText());
            var amountCents = root.path("amountCents").asLong();
            var currency = root.path("currency").asText("BRL");

            handler.onPaymentAuthorized(eventId, occurredAt, correlationId, paymentId, accountId, amountCents, currency);
        } catch (Exception e) {
            log.error("failed_to_consume message={}", message, e);
        }
    }
}
