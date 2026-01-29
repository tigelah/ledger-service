package br.com.tigelah.ledgerservice.application.handlers;

import br.com.tigelah.ledgerservice.application.usecase.RecordHoldFromAuthorizationUseCase;
import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import br.com.tigelah.ledgerservice.domain.ports.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationEventHandlerTest {

    @Test
    void publishes_ledger_entry_recorded() {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        var topicRef = new AtomicReference<String>();
        var keyRef = new AtomicReference<String>();
        var payloadRef = new AtomicReference<String>();

        EventPublisher publisher = (t, k, p) -> { topicRef.set(t); keyRef.set(k); payloadRef.set(p); };

        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) { return Optional.of(new LedgerAccount(id, 1000, "BRL", Instant.now())); }
            @Override public void save(LedgerAccount account) {}
        };

        EntryRepository entries = new EntryRepository() {
            boolean exists = false;
            @Override public void append(LedgerEntry entry) { exists = true; }
            @Override public List<LedgerEntry> findByAccountId(UUID accountId) { return List.of(); }
            @Override public long sumHoldDebits(UUID accountId) { return 0; }
            @Override public boolean existsHoldForPayment(UUID accountId, UUID paymentId) { return exists; }
        };

        var recordHold = new RecordHoldFromAuthorizationUseCase(accounts, entries,
                Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC));

        var handler = new AuthorizationEventHandler(recordHold, publisher, mapper);

        var eventId = UUID.randomUUID();
        handler.onPaymentAuthorized(eventId, "2030-01-01T00:00:00Z", "c1", UUID.randomUUID(), UUID.randomUUID(), 10, "BRL");

        assertNotNull(topicRef.get());
        assertNotNull(keyRef.get());
        assertNotNull(payloadRef.get());
    }
}
