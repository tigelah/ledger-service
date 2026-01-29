package br.com.tigelah.ledgerservice.application.handlers;

import br.com.tigelah.ledgerservice.application.usecase.RecordHoldFromAuthorizationUseCase;
import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import br.com.tigelah.ledgerservice.domain.ports.EventPublisher;
import br.com.tigelah.ledgerservice.infrastructure.messaging.Topics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationEventHandlerTest {

    @Test
    void publishes_ledger_entry_recorded() {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        var topicRef = new AtomicReference<String>();
        var keyRef = new AtomicReference<String>();
        var payloadRef = new AtomicReference<String>();

        EventPublisher publisher = (t, k, p) -> {
            topicRef.set(t);
            keyRef.set(k);
            payloadRef.set(p);
        };

        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) {
                return Optional.of(new LedgerAccount(id, 1000, "BRL", Instant.parse("2030-01-01T00:00:00Z")));
            }
            @Override public void save(LedgerAccount account) {}
        };

        EntryRepository entries = new EntryRepository() {
            private final Set<UUID> holdsByPaymentId = new HashSet<>();

            @Override public boolean existsHoldForPayment(UUID accountId, UUID paymentId) {
                return holdsByPaymentId.contains(paymentId);
            }

            @Override public void append(LedgerEntry entry) {
                holdsByPaymentId.add(entry.paymentId());
            }

            @Override public List<LedgerEntry> findByAccountId(UUID accountId) { return List.of(); }

            @Override public long sumHoldDebits(UUID accountId) { return 0; }
        };

        var recordHold = new RecordHoldFromAuthorizationUseCase(
                accounts,
                entries,
                Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC)
        );

        var handler = new AuthorizationEventHandler(recordHold, publisher, mapper);

        var eventId = UUID.randomUUID();
        var paymentId = UUID.randomUUID();
        var accountId = UUID.randomUUID();

        handler.onPaymentAuthorized(eventId, "2030-01-01T00:00:00Z", "c1",
                paymentId, accountId, 10, "BRL");

        assertEquals(Topics.LEDGER_ENTRY_RECORDED, topicRef.get());
        assertEquals(paymentId.toString(), keyRef.get());
        assertNotNull(payloadRef.get());
        assertFalse(payloadRef.get().isBlank());
    }
}