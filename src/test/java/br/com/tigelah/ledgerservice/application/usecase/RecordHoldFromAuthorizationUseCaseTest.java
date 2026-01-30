package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecordHoldFromAuthorizationUseCaseTest {

    @Test
    void records_hold_and_is_idempotent() {
        var accountId = UUID.randomUUID();
        var paymentId = UUID.randomUUID();
        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);

        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) {
                return Optional.of(new LedgerAccount(accountId, 1000, "BRL", Instant.parse("2030-01-01T00:00:00Z")));
            }
            @Override public void save(LedgerAccount account) {}
        };

        var appended = new ArrayList<LedgerEntry>();
        EntryRepository entries = new EntryRepository() {
            boolean exists = false;
            @Override public void append(LedgerEntry entry) { appended.add(entry); exists = true; }
            @Override public long sumHoldDebits(UUID accountId) { return 0; }
            @Override public boolean existsHoldForPayment(UUID accountId, UUID paymentId) { return exists; }

            @Override
            public boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType) {
                return false;
            }

            @Override
            public long sumCaptureDebits(UUID accountId) {
                return 0;
            }
        };

        var uc = new RecordHoldFromAuthorizationUseCase(accounts, entries, clock);

        var e1 = uc.execute(accountId, paymentId, 10, "BRL", "c1");
        assertNotNull(e1);
        assertEquals("HOLD", e1.entryType().name());
        assertEquals("DEBIT", e1.direction().name());
        assertEquals(1, appended.size());

        var e2 = uc.execute(accountId, paymentId, 10, "BRL", "c1");
        assertNull(e2);
        assertEquals(1, appended.size());
    }

    @Test
    void currency_mismatch() {
        var accountId = UUID.randomUUID();
        var paymentId = UUID.randomUUID();
        var clock = Clock.systemUTC();

        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) {
                return Optional.of(new LedgerAccount(accountId, 1000, "BRL", Instant.now()));
            }
            @Override public void save(LedgerAccount account) {}
        };

        EntryRepository entries = new EntryRepository() {
            @Override public void append(LedgerEntry entry) {}
            @Override public long sumHoldDebits(UUID accountId) { return 0; }
            @Override public boolean existsHoldForPayment(UUID accountId, UUID paymentId) { return false; }

            @Override
            public boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType) {
                return false;
            }

            @Override
            public long sumCaptureDebits(UUID accountId) {
                return 0;
            }
        };

        var uc = new RecordHoldFromAuthorizationUseCase(accounts, entries, clock);
        var ex = assertThrows(IllegalArgumentException.class, () -> uc.execute(accountId, paymentId, 10, "USD", "c1"));
        assertEquals("currency_mismatch", ex.getMessage());
    }
}
