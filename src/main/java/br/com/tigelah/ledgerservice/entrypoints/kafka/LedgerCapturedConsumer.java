package br.com.tigelah.ledgerservice.entrypoints.kafka;

import br.com.tigelah.ledgerservice.application.usecase.RecordCaptureUseCase;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class LedgerCapturedConsumer {
    private static final Logger log = LoggerFactory.getLogger(LedgerCapturedConsumer.class);

    private final ObjectMapper mapper;
    private final RecordCaptureUseCase recordCapture;

    public LedgerCapturedConsumer(ObjectMapper mapper, RecordCaptureUseCase recordCapture) {
        this.mapper = mapper;
        this.recordCapture = recordCapture;
    }

    @KafkaListener(topics = { "payment.captured" }, groupId = "${spring.kafka.consumer.group-id:ledger-service}")
    @Transactional
    public void onMessage(String message) {
        try {
            JsonNode root = mapper.readTree(message);

            var paymentId = UUID.fromString(root.path("paymentId").asText());
            var accountId = UUID.fromString(root.path("accountId").asText());
            var amountCents = root.path("amountCents").asLong();
            var currency = root.path("currency").asText("BRL");
            var correlationId = root.path("correlationId").asText("n/a");

            var userId = root.path("userId").asText("");
            var panHash = root.path("panHash").asText("");
            var panLast4 = root.path("panLast4").asText("");

            LimitScopeType scopeType;
            String scopeKey;

            if (!userId.isBlank()) {
                scopeType = LimitScopeType.USER;
                scopeKey = userId;
            } else if (!panHash.isBlank()) {
                scopeType = LimitScopeType.PAN;
                scopeKey = panHash;
            } else {
                scopeType = LimitScopeType.PAN;
                scopeKey = panLast4;
            }

            recordCapture.execute(accountId, paymentId, amountCents, currency, correlationId, scopeType, scopeKey);
        } catch (Exception e) {
            log.error("failed_to_consume payment.captured message={}", message, e);
        }
    }
}