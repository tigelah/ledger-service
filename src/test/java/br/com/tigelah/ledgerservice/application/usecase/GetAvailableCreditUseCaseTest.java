package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
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
            @Override public void append(br.com.tigelah.ledgerservice.domain.model.LedgerEntry entry) {}
            @Override public List<br.com.tigelah.ledgerservice.domain.model.LedgerEntry> findByAccountId(UUID accountId) { return List.of(); }
            @Override public long sumHoldDebits(UUID accountId) { return 30; }
            @Override public boolean existsHoldForPayment(UUID accountId, UUID paymentId) { return false; }
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
            @Override public void append(br.com.tigelah.ledgerservice.domain.model.LedgerEntry entry) {}
            @Override public List<br.com.tigelah.ledgerservice.domain.model.LedgerEntry> findByAccountId(UUID accountId) { return List.of(); }
            @Override public long sumHoldDebits(UUID accountId) { return 0; }
            @Override public boolean existsHoldForPayment(UUID accountId, UUID paymentId) { return false; }
        };

        var uc = new GetAvailableCreditUseCase(accounts, entries);
        var ex = assertThrows(IllegalArgumentException.class, () -> uc.execute(accountId));
        assertEquals("account_not_found", ex.getMessage());
    }
}
