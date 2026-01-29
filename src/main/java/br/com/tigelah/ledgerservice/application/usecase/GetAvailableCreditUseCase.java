package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;

import java.util.UUID;

public class GetAvailableCreditUseCase {
    private final AccountRepository accounts;
    private final EntryRepository entries;

    public GetAvailableCreditUseCase(AccountRepository accounts, EntryRepository entries) {
        this.accounts = accounts;
        this.entries = entries;
    }

    public AvailableCredit execute(UUID accountId) {
        var account = accounts.findById(accountId).orElseThrow(() -> new IllegalArgumentException("account_not_found"));
        long holds = entries.sumHoldDebits(accountId);
        long available = account.creditLimitCents() - holds;
        if (available < 0) available = 0;
        return new AvailableCredit(account.id(), available, account.currency());
    }

    public record AvailableCredit(UUID accountId, long availableCents, String currency) {}
}
