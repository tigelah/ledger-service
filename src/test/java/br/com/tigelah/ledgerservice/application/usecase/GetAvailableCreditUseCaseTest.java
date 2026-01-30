package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.EntryType;
import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GetAvailableCreditUseCaseTest {

    @Test
    void calculates_available_limit_minus_holds() {
        var accountId = UUID.randomUUID();

        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) {
                return Optional.of(new LedgerAccount(accountId, 100, "BRL", Instant.parse("2030-01-01T00:00:00Z")));
            }
            @Override public void save(LedgerAccount account) {}
        };

        EntryRepository entries = new EntryRepository() {
            @Override public void append(LedgerEntry entry) {}
            @Override public long sumHoldDebits(UUID accountId) { return 30; }
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

        var uc = new GetAvailableCreditUseCase(accounts, entries);
        var out = uc.execute(accountId);

        assertEquals(70, out.availableCents());
        assertEquals("BRL", out.currency());
    }

    @Test
    void account_not_found() {
        var accountId = UUID.randomUUID();
        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) { return Optional.empty(); }
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

        var uc = new GetAvailableCreditUseCase(accounts, entries);
        var ex = assertThrows(IllegalArgumentException.class, () -> uc.execute(accountId));
        assertEquals("account_not_found", ex.getMessage());
    }

    @Test
    void available_credit_considers_hold_and_capture() {
        var accountId = UUID.randomUUID();

        AccountRepository accounts = new AccountRepository() {
            @Override public Optional<LedgerAccount> findById(UUID id) {
                return Optional.of(new LedgerAccount(accountId, 100, "BRL", Instant.parse("2030-01-01T00:00:00Z")));
            }
            @Override public void save(LedgerAccount account) {}
        };

        EntryRepository entries = new EntryRepository() {
            @Override public void append(LedgerEntry entry) {}
            @Override public boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType) { return false; }
            @Override public long sumHoldDebits(UUID accountId) { return 30; }

            @Override
            public boolean existsHoldForPayment(UUID accountId, UUID paymentId) {
                return false;
            }

            @Override public long sumCaptureDebits(UUID accountId) { return 20; }
        };

        var uc = new GetAvailableCreditUseCase(accounts, entries);
        var out = uc.execute(accountId);

        assertEquals(50, out.availableCents()); // 100 - 30 - 20
        assertEquals(30, out.holdsCents());
        assertEquals(20, out.capturedCents());
    }
}
