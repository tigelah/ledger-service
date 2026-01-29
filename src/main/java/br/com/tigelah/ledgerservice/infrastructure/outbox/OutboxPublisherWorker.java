package br.com.tigelah.ledgerservice.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Component
public class OutboxPublisherWorker {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherWorker.class);

    private final JpaOutboxRepository outbox;
    private final KafkaTemplate<String, String> kafka;
    private final Clock clock;

    public OutboxPublisherWorker(JpaOutboxRepository outbox, KafkaTemplate<String, String> kafka, Clock clock) {
        this.outbox = outbox;
        this.kafka = kafka;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:2000}")
    @Transactional
    public void tick() {
        var pending = outbox.findPending();
        if (pending.isEmpty()) return;

        for (var e : pending) {
            try {
                kafka.send(e.topic, e.messageKey, e.payloadJson);
                e.status = OutboxStatus.SENT.name();
                e.sentAt = clock.instant();
                outbox.save(e);
            } catch (Exception ex) {
                e.attempts += 1;
                e.status = OutboxStatus.FAILED.name();
                outbox.save(e);
                log.warn("outbox_publish_failed id={} topic={} attempts={}", e.id, e.topic, e.attempts, ex);
            }
        }
    }
}
