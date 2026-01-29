package br.com.tigelah.ledgerservice.infrastructure.messaging;

import br.com.tigelah.ledgerservice.domain.ports.EventPublisher;
import br.com.tigelah.ledgerservice.infrastructure.outbox.OutboxWriter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OutboxEventPublisher implements EventPublisher {
    private final OutboxWriter outbox;

    public OutboxEventPublisher(OutboxWriter outbox) {
        this.outbox = outbox;
    }

    @Override
    public void publish(String topic, String key, String payloadJson) {
        outbox.enqueue("ledger", UUID.nameUUIDFromBytes(key.getBytes()), topic, key, payloadJson);
    }
}
