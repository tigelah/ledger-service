package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.EntryDirection;
import br.com.tigelah.ledgerservice.domain.model.EntryType;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;

import java.time.Clock;
import java.util.UUID;

public class RecordHoldFromAuthorizationUseCase {
    private final AccountRepository accounts;
    private final EntryRepository entries;
    private final Clock clock;

    public RecordHoldFromAuthorizationUseCase(AccountRepository accounts, EntryRepository entries, Clock clock) {
        this.accounts = accounts;
        this.entries = entries;
        this.clock = clock;
    }

    public LedgerEntry execute(UUID accountId, UUID paymentId, long amountCents, String currency, String correlationId) {
        if (amountCents <= 0) throw new IllegalArgumentException("amount_must_be_positive");
        var account = accounts.findById(accountId).orElseThrow(() -> new IllegalArgumentException("account_not_found"));

        if (!account.currency().equalsIgnoreCase(currency)) throw new IllegalArgumentException("currency_mismatch");

        if (entries.existsHoldForPayment(accountId, paymentId)) return null; // idempotent

        var entry = new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                paymentId,
                EntryType.HOLD,
                EntryDirection.DEBIT,
                amountCents,
                account.currency(),
                clock.instant(),
                correlationId
        );
        entries.append(entry);
        return entry;
    }
}
